// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.debug.core.model;

import java.util.ArrayList;
import java.util.List;

import org.chromium.debug.core.ChromiumDebugPlugin;
import org.chromium.debug.core.util.ChromiumDebugPluginUtil;
import org.chromium.sdk.CallFrame;
import org.chromium.sdk.DebugContext;
import org.chromium.sdk.DebugContext.StepAction;
import org.chromium.sdk.ExceptionData;
import org.chromium.sdk.InvalidContextException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

/**
 * This class represents the only Chromium V8 VM thread.
 */
public class JavascriptThread extends DebugElementImpl.WithConnected
    implements IThread, IAdaptable {

  private static final StackFrame[] EMPTY_FRAMES = new StackFrame[0];

  /**
   * Breakpoints this thread is suspended at or <code>null</code> if none.
   */
  private IBreakpoint[] breakpoints;

  /**
   * Whether this thread is stepping. V8 does not provide information if the
   * thread is actually stepping or it is running past the last "steppable"
   * statement.
   */
  private boolean isStepping = false;

  /**
   * Cached stack
   */
  private StackFrameBase[] stackFrames;

  /**
   * Holds 'suspended' state of the thread. As such has a getter to {@link DebugContext}.
   * It also keep references to basic enclosing objects.
   */
  public interface SuspendedState {
    JavascriptThread getThread();

    ConnectedTargetData getConnectedTargetData();

    DebugContext getDebugContext();
  }

  /**
   * Constructs a new thread for the given target
   *
   * @param connectedTargetData this thread is created for
   */
  public JavascriptThread(ConnectedTargetData connectedTargetData) {
    super(connectedTargetData);
  }

  public StackFrameBase[] getStackFrames() throws DebugException {
    try {
      ensureStackFrames(getConnectedData().getThreadSuspendedState(this));
      return stackFrames;
    } catch (InvalidContextException e) {
      return new StackFrame[0];
    }
  }

  public void reset() {
    this.stackFrames = null;
  }

  private void ensureStackFrames(JavascriptThread.SuspendedState threadState) {
    if (stackFrames == null) {
      if (threadState == null) {
        this.stackFrames = EMPTY_FRAMES;
      } else {
        this.stackFrames = wrapStackFrames(threadState);
      }
    }
  }

  private static StackFrameBase[] wrapStackFrames(JavascriptThread.SuspendedState threadState) {
    DebugContext debugContext = threadState.getDebugContext();
    List<? extends CallFrame> jsFrames = debugContext.getCallFrames();
    List<StackFrameBase> result = new ArrayList<StackFrameBase>(jsFrames.size() + 1);

    ExceptionData exceptionData = debugContext.getExceptionData();
    if (exceptionData != null) {
      // Add fake 'throw exception' frame.
      EvaluateContext evaluateContext =
          new EvaluateContext(debugContext.getGlobalEvaluateContext(), threadState);
      result.add(new ExceptionStackFrame(evaluateContext, exceptionData));
    }
    for (CallFrame jsFrame : jsFrames) {
      result.add(new StackFrame(threadState, jsFrame));
    }
    return ChromiumDebugPluginUtil.toArray(result, StackFrameBase.class);
  }

  /**
   * A fake stackframe that represents 'throwing exception'. It's a frame that holds an exception
   * as its only variable. This might be the only means to expose exception value to user because
   * exception may be raised with no frames on stack (e.g. compile error).
   */
  private static class ExceptionStackFrame extends StackFrameBase {
    private final IVariable[] variables;
    private final ExceptionData exceptionData;

    private ExceptionStackFrame(EvaluateContext evaluateContext, ExceptionData exceptionData) {
      super(evaluateContext);
      this.exceptionData = exceptionData;

      Variable variable = Variable.NamedHolder.forException(evaluateContext, exceptionData);
      variables = new IVariable[] { variable };
    }

    @Override
    public IVariable[] getVariables() throws DebugException {
      return variables;
    }

    @Override
    public boolean hasVariables() throws DebugException {
      return variables.length > 0;
    }

    @Override
    public int getLineNumber() throws DebugException {
      return -1;
    }

    @Override
    public int getCharStart() throws DebugException {
      return -1;
    }

    @Override
    public int getCharEnd() throws DebugException {
      return getCharStart();
    }

    @Override
    public String getName() throws DebugException {
      return "<throwing exception>";
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof ExceptionStackFrame == false) {
        return false;
      }
      ExceptionStackFrame other = (ExceptionStackFrame) obj;
      return this.exceptionData.equals(other.exceptionData);
    }

    @Override
    public int hashCode() {
      return this.exceptionData.hashCode();
    }
  }

  public boolean hasStackFrames() throws DebugException {
    return isSuspended();
  }

  public int getPriority() throws DebugException {
    return 0;
  }

  public IStackFrame getTopStackFrame() throws DebugException {
    IStackFrame[] frames = getStackFrames();
    if (frames.length > 0) {
      return frames[0];
    }
    return null;
  }

  public String getName() throws DebugException {
    return getDebugTarget().getLabelProvider().getThreadLabel(this);
  }

  public IBreakpoint[] getBreakpoints() {
    if (breakpoints == null) {
      return new IBreakpoint[0];
    }
    return breakpoints;
  }

  protected void setBreakpoints(IBreakpoint[] breakpoints) {
    this.breakpoints = breakpoints;
  }

  public boolean canResume() {
    return isSuspended();
  }

  public boolean canSuspend() {
    return getDebugTarget().canSuspend();
  }

  public boolean isSuspended() {
    return getDebugTarget().isSuspended();
  }

  public void resume() throws DebugException {
    setStepping(false);
    getDebugTarget().resume();
  }

  public void suspend() throws DebugException {
    getDebugTarget().suspend();
  }

  public boolean canStepInto() {
    return isSuspended();
  }

  public boolean canStepOver() {
    return isSuspended();
  }

  public boolean canStepReturn() {
    return isSuspended();
  }

  public boolean isStepping() {
    return isStepping;
  }

  private void step(StepAction stepAction, int detail) throws DebugException {
    DebugContext debugContext = getConnectedData().getDebugContext();
    if (debugContext == null) {
      throw new DebugException(new Status(IStatus.ERROR, ChromiumDebugPlugin.PLUGIN_ID,
          "Step attempted while not suspended")); //$NON-NLS-1$
    }
    setStepping(true);
    debugContext.continueVm(stepAction, 1, null);
    // The suspend event should be fired once the backtrace is ready
    // (in BacktraceProcessor).
    getConnectedData().fireResumeEvent(detail);
  }

  public void stepInto() throws DebugException {
    step(StepAction.IN, DebugEvent.STEP_INTO);
  }

  public void stepOver() throws DebugException {
    step(StepAction.OVER, DebugEvent.STEP_OVER);
  }

  public void stepReturn() throws DebugException {
    step(StepAction.OUT, DebugEvent.STEP_RETURN);
  }

  public boolean canTerminate() {
    return getDebugTarget().canTerminate();
  }

  public boolean isTerminated() {
    return getDebugTarget().isTerminated();
  }

  public void terminate() throws DebugException {
    getDebugTarget().terminate();
  }

  /**
   * Sets whether this thread is stepping.
   */
  protected void setStepping(boolean stepping) {
    isStepping = stepping;
  }

  EvaluateContext getEvaluateContext() {
    ConnectedTargetData targetConnectedData = getConnectedData();
    JavascriptThread.SuspendedState threadState = targetConnectedData.getThreadSuspendedState(this);
    if (threadState == null) {
      return null;
    }
    return new EvaluateContext(threadState.getDebugContext().getGlobalEvaluateContext(),
        threadState);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object getAdapter(Class adapter) {
    if (adapter == EvaluateContext.class) {
      return getEvaluateContext();
    }
    return super.getAdapter(adapter);
  }
}
