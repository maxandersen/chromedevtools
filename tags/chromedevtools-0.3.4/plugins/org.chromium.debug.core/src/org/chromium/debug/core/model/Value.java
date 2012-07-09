// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.debug.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.chromium.debug.core.util.JsValueStringifier;
import org.chromium.sdk.JsArray;
import org.chromium.sdk.JsEvaluateContext;
import org.chromium.sdk.JsObject;
import org.chromium.sdk.JsValue;
import org.chromium.sdk.JsVariable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.IValueDetailListener;

/**
 * A generic (non-array) implementation of IValue using a JsValue instance.
 */
public class Value extends ValueBase.ValueWithLazyVariables {

  public static Value create(EvaluateContext evaluateContext, JsValue value,
      ValueBase.ValueAsHostObject hostObject) {
    if (JsValue.Type.TYPE_ARRAY == value.getType()) {
      return new ArrayValue(evaluateContext, (JsArray) value, hostObject);
    }
    return new Value(evaluateContext, value, hostObject);
  }

  private final JsValue value;

  private final ValueBase.ValueAsHostObject hostObject;

  private final DetailBuilder detailBuilder = new DetailBuilder();

  protected Value(EvaluateContext evaluateContext, JsValue value,
      ValueBase.ValueAsHostObject hostObject) {
    super(evaluateContext);
    this.value = value;
    this.hostObject = hostObject;
  }

  public String getReferenceTypeName() throws DebugException {
    return value.getType().toString();
  }

  public String getValueString() {
    String valueText = JsValueStringifier.toVisibleString(value);
    if (value.asObject() != null) {
      String ref = value.asObject().getRefId();
      if (ref != null) {
        valueText = valueText + "  (id=" + ref + ")";
      }
    }
    return valueText;
  }

  // This method could be blocking -- it gets called from a worker thread.
  // All data should be prepared here.
  protected IVariable[] calculateVariables() {
    JsObject asObject = value.asObject();
    if (asObject == null) {
      return EMPTY_VARIABLES;
    }
    return StackFrame.wrapVariables(getEvaluateContext(),
        asObject.getProperties(), Collections.<String>emptySet(),
        asObject.getInternalProperties(), hostObject);
  }

  public boolean hasVariables() throws DebugException {
    return value.asObject() != null;
  }

  public boolean isAllocated() throws DebugException {
    return true;
  }

  @Override public Value asRealValue() {
    return this;
  }

  public JsValue getJsValue() {
    return value;
  }

  /**
   * Called from Worker thread.
   * @param listener will be called from various threads (its implementation is thread-safe)
   */
  public void computeDetailAsync(IValueDetailListener listener) {
    detailBuilder.buildDetailAsync(listener);
  }

  public boolean isTruncated() {
    return this.value.isTruncated() || detailBuilder.getCurrentDetailWrapper().isTruncated();
  }

  protected ValueBase.ValueAsHostObject getHostObject() {
    return hostObject;
  }

  public void reloadBiggerValue(final ReloadValueCallback callback) {
    List<JsValue> jsValueList = new ArrayList<JsValue>(2);
    if (this.value.isTruncated()) {
      jsValueList.add(this.value);
    }
    JsValue detailValue = detailBuilder.getCurrentDetailWrapper().getJsValue();
    if (detailValue != null && detailValue.isTruncated() && !jsValueList.contains(detailValue)) {
      jsValueList.add(detailValue);
    }

    ReloadBiggerValueProcess process = new ReloadBiggerValueProcess(callback);
    process.start(jsValueList);
  }

  public interface ReloadValueCallback {
    void done(boolean changed);
  }

  /**
   * An implementation of process that conducts reloading of several JsValue's.
   * We need such class because technically the Value contains the actual value and
   * a value of its detail (toString representation), both of which may have been truncated.
   */
  private static class ReloadBiggerValueProcess {
    private final ReloadValueCallback callback;
    private int counter;
    private boolean somethingChanged;

    ReloadBiggerValueProcess(ReloadValueCallback callback) {
      this.callback = callback;
    }

    void start(List<JsValue> jsValueList) {
      if (jsValueList.isEmpty()) {
        callback.done(false);
        return;
      }

      this.counter = jsValueList.size();
      this.somethingChanged = false;

      for (final JsValue jsValue : jsValueList) {
        final String originalValue = jsValue.getValueString();
        jsValue.reloadHeavyValue(new JsValue.ReloadBiggerCallback() {
              public void done() {
                String newValueString = jsValue.getValueString();
                boolean changed = newValueString != null && !newValueString.equals(originalValue);
                boolean weAreLast;
                boolean somethingChangedSaved;
                synchronized (ReloadBiggerValueProcess.this) {
                  counter--;
                  somethingChanged |= changed;
                  somethingChangedSaved = somethingChanged;
                  weAreLast = counter == 0;
                }
                if (weAreLast) {
                  callback.done(somethingChangedSaved);
                }
              }
            }, null);
      }
    }
  }

  /**
   * A small abstraction over detail value. Internally the value may be a plain string
   * or backed by a JsValue instance.
   */
  private abstract static class DetailWrapper {
    abstract boolean isTruncated();
    abstract JsValue getJsValue();
    abstract String getStringValue();
  }

  private static final DetailWrapper NO_DETAILS_WRAPPER = new DetailWrapper() {
    boolean isTruncated() {
      return false;
    }
    JsValue getJsValue() {
      return null;
    }
    String getStringValue() {
      return null;
    }
  };


  /**
   * Builds the string detail, possibly asynchronously. The details may be truncated
   * and reloaded in full later.
   */
  private class DetailBuilder {
    private volatile DetailWrapper detailWrapper = NO_DETAILS_WRAPPER;

    private static final String TO_STRING_ARGUMENT = "object";
    private static final String TO_STRING_EXPRESSION = "String(" + TO_STRING_ARGUMENT + ")";


    DetailWrapper getCurrentDetailWrapper() {
      return detailWrapper;
    }

    void buildDetailAsync(final IValueDetailListener listener) {
      DetailWrapper alreadyCalculatedDetail = this.detailWrapper;
      if (alreadyCalculatedDetail != NO_DETAILS_WRAPPER) {
        listener.detailComputed(Value.this, alreadyCalculatedDetail.getStringValue());
        return;
      }
      JsObject jsObject = getJsValue().asObject();
      if (jsObject == null) {
        jsValueDetailIsBuilt(getJsValue(), listener);
        return;
      }
      String objectRefId = jsObject.getRefId();
      if (objectRefId == null) {
        stringDetailIsBuilt("", listener);
        return;
      }

      if (getSuspendedState().isDismissed()) {
        stringDetailIsBuilt("", listener);
        return;
      }

      Map<String, String> additionalContext =
          Collections.singletonMap(TO_STRING_ARGUMENT, objectRefId);

      JsEvaluateContext.EvaluateCallback evaluateCallback =
          new JsEvaluateContext.EvaluateCallback() {
        public void success(JsVariable variable) {
          jsValueDetailIsBuilt(variable.getValue(), listener);
        }
        public void failure(String errorMessage) {
          stringDetailIsBuilt(errorMessage, listener);
        }
      };

      JsEvaluateContext evaluateContext =
          getSuspendedState().getDebugContext().getGlobalEvaluateContext();

      evaluateContext.evaluateAsync(TO_STRING_EXPRESSION, additionalContext,
          evaluateCallback, null);
    }

    private void stringDetailIsBuilt(final String detailString, IValueDetailListener listener) {
      DetailWrapper detailWrapper = new DetailWrapper() {
        boolean isTruncated() {
          return false;
        }
        String getStringValue() {
          return detailString;
        }
        JsValue getJsValue() {
          return null;
        }
      };
      detailIsBuiltImpl(detailWrapper, listener);
    }

    private void jsValueDetailIsBuilt(final JsValue detailValue, IValueDetailListener listener) {
      DetailWrapper detailWrapper = new DetailWrapper() {
        boolean isTruncated() {
          return detailValue.isTruncated();
        }
        String getStringValue() {
          return detailValue.getValueString();
        }
        JsValue getJsValue() {
          return detailValue;
        }
      };
      detailIsBuiltImpl(detailWrapper, listener);
    }

    private void detailIsBuiltImpl(DetailWrapper detailWrapper, IValueDetailListener listener) {
      // We may override value concurrently, but it's ok.
      this.detailWrapper = detailWrapper;
      listener.detailComputed(Value.this, detailWrapper.getStringValue());
    }
  }
}