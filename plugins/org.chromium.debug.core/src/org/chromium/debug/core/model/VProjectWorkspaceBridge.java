// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.debug.core.model;

import java.util.Collection;

import org.chromium.debug.core.ChromiumDebugPlugin;
import org.chromium.debug.core.util.ChromiumDebugPluginUtil;
import org.chromium.sdk.Breakpoint;
import org.chromium.sdk.JavascriptVm;
import org.chromium.sdk.Script;
import org.chromium.sdk.JavascriptVm.BreakpointCallback;
import org.chromium.sdk.JavascriptVm.ScriptsCallback;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;

/**
 * Virtual project-supporting implementation of {@link WorkspaceBridge}.
 */
public class VProjectWorkspaceBridge implements WorkspaceBridge {
  /** The debug model ID. */
  public static final String DEBUG_MODEL_ID = "org.chromium.debug"; //$NON-NLS-1$

  public static class FactoryImpl implements Factory {
    private final String projectNameBase;

    public FactoryImpl(String projectNameBase) {
      this.projectNameBase = projectNameBase;
    }

    public WorkspaceBridge attachedToVm(DebugTargetImpl debugTargetImpl,
        JavascriptVm javascriptVm) {
      // We might want to add URL or something to project name.
      return new VProjectWorkspaceBridge(projectNameBase, debugTargetImpl, javascriptVm);
    }

    public String getDebugModelIdentifier() {
      return DEBUG_MODEL_ID;
    }
  }

  private final IProject debugProject;
  private final JavascriptVm javascriptVm;
  private final ResourceManager resourceManager;
  private final BreakpointRegistry breakpointRegistry = new BreakpointRegistry();
  private final DebugTargetImpl debugTargetImpl;

  public VProjectWorkspaceBridge(String projectName, DebugTargetImpl debugTargetImpl,
      JavascriptVm javascriptVm) {
    this.debugTargetImpl = debugTargetImpl;
    this.javascriptVm = javascriptVm;
    this.debugProject = ChromiumDebugPluginUtil.createEmptyProject(projectName);
    this.resourceManager = new ResourceManager(debugProject, breakpointRegistry);
    ILaunch launch = debugTargetImpl.getLaunch();
    launch.setSourceLocator(sourceLocator);
  }

  public void launchRemoved() {
    if (debugProject != null) {
      ChromiumDebugPluginUtil.deleteVirtualProjectAsync(debugProject);
    }
  }

  public void beforeDetach() {
    IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
    IBreakpoint[] breakpoints =
        breakpointManager.getBreakpoints(DEBUG_MODEL_ID);
    for (IBreakpoint bp : breakpoints) {
      ChromiumLineBreakpoint clb = (ChromiumLineBreakpoint) bp;
      if (clb.getBrowserBreakpoint() != null &&
          clb.getBrowserBreakpoint().getId() != Breakpoint.INVALID_ID) {
        clb.getBrowserBreakpoint().clear(null);
      }
    }
    try {
      breakpointManager.removeBreakpoints(breakpoints, true);
    } catch (CoreException e) {
      ChromiumDebugPlugin.log(e);
    }
  }

  public void handleVmResetEvent() {
    resourceManager.clear();
  }

  public void scriptLoaded(Script newScript) {
    resourceManager.addScript(newScript);
  }

  public void reloadScriptsAtStart() {
    javascriptVm.getScripts(new ScriptsCallback() {
      public void failure(String errorMessage) {
        ChromiumDebugPlugin.logError(errorMessage);
      }

      public void success(Collection<Script> scripts) {
        if (!javascriptVm.isAttached()) {
          return;
        }
        for (Script script : scripts) {
          resourceManager.addScript(script);
        }
      }
    });
  }

  public IFile getScriptResource(Script script) {
    return resourceManager.getResource(script);
  }

  public BreakpointHandler getBreakpointHandler() {
    return breakpointHandler;
  }

  private final BreakpointHandler breakpointHandler = new BreakpointHandler() {
    public boolean supportsBreakpoint(IBreakpoint breakpoint) {
      return DEBUG_MODEL_ID.equals(breakpoint.getModelIdentifier()) &&
          !debugTargetImpl.isDisconnected();
    }
    public void breakpointAdded(IBreakpoint breakpoint) {
      if (!supportsBreakpoint(breakpoint)) {
        return;
      }
      try {
        if (breakpoint.isEnabled()) {
          // Class cast is ensured by the supportsBreakpoint implementation
          final ChromiumLineBreakpoint lineBreakpoint = (ChromiumLineBreakpoint) breakpoint;
          IFile file = (IFile) breakpoint.getMarker().getResource();
          if (resourceManager.isAddingFile(file)) {
            return; // restoring breakpoints in progress
          }
          final Script script = resourceManager.getScript(file);
          if (script == null) {
            // Might be a script from a different debug target
            return;
          }
          // ILineBreakpoint lines are 1-based while V8 lines are 0-based
          final int line = (lineBreakpoint.getLineNumber() - 1) + script.getStartLine();
          BreakpointCallback callback = new BreakpointCallback() {
            public void success(Breakpoint breakpoint) {
              lineBreakpoint.setBreakpoint(breakpoint);
              breakpointRegistry.add(script, line, breakpoint);
            }

            public void failure(String errorMessage) {
              ChromiumDebugPlugin.logError(errorMessage);
            }
          };
          if (script.getName() != null) {
            javascriptVm.setBreakpoint(Breakpoint.Type.SCRIPT_NAME,
                script.getName(),
                line,
                Breakpoint.EMPTY_VALUE,
                breakpoint.isEnabled(),
                lineBreakpoint.getCondition(),
                lineBreakpoint.getIgnoreCount(),
                callback);
          } else {
            javascriptVm.setBreakpoint(Breakpoint.Type.SCRIPT_ID,
                String.valueOf(script.getId()),
                line,
                Breakpoint.EMPTY_VALUE,
                breakpoint.isEnabled(),
                lineBreakpoint.getCondition(),
                lineBreakpoint.getIgnoreCount(),
                callback);
          }
        }
      } catch (CoreException e) {
        ChromiumDebugPlugin.log(e);
      }
    }

    public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
      if (!supportsBreakpoint(breakpoint)) {
        return;
      }
      // Class cast is ensured by the supportsBreakpoint implementation
      ((ChromiumLineBreakpoint) breakpoint).changed();
    }

    public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
      if (!supportsBreakpoint(breakpoint)) {
        return;
      }
      try {
        if (breakpoint.isEnabled()) {
          // Class cast is ensured by the supportsBreakpoint implementation
          ChromiumLineBreakpoint lineBreakpoint = (ChromiumLineBreakpoint) breakpoint;
          lineBreakpoint.clear();
          breakpointRegistry.remove(
              resourceManager.getScript((IFile) breakpoint.getMarker().getResource()),
              lineBreakpoint.getLineNumber() - 1,
              lineBreakpoint.getBrowserBreakpoint());
        }
      } catch (CoreException e) {
        ChromiumDebugPlugin.log(e);
      }
    }

    public void breakpointsHit(Collection<? extends Breakpoint> breakpointsHit) {
      if (breakpointsHit.isEmpty()) {
        return;
      }
      IBreakpoint[] breakpoints =
          DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(DEBUG_MODEL_ID);
      for (IBreakpoint breakpoint : breakpoints) {
        ChromiumLineBreakpoint jsBreakpoint = (ChromiumLineBreakpoint) breakpoint;
        if (breakpointsHit.contains(jsBreakpoint.getBrowserBreakpoint())) {
          jsBreakpoint.setIgnoreCount(-1); // reset ignore count as we've hit it
        }
      }
    }
  };

  /**
   * This very simple source locator works because we provide our own source files.
   * We'll have to try harder, once we link with resource js files.
   */
  private final ISourceLocator sourceLocator = new ISourceLocator() {
    public Object getSourceElement(IStackFrame stackFrame) {
      if (stackFrame instanceof StackFrame == false) {
        return null;
      }
      StackFrame jsStackFrame = (StackFrame) stackFrame;

      Script script = jsStackFrame.getCallFrame().getScript();
      if (script == null) {
        return null;
      }

      return resourceManager.getResource(script);
    }
  };
}
