// Generated source.
// Generator: org.chromium.sdk.internal.wip.tools.protocolgenerator.Generator
// Origin: http://svn.webkit.org/repository/webkit/trunk/Source/WebCore/inspector/Inspector.json@135591

package org.chromium.sdk.internal.wip.protocol.input;

public class GeneratedParserInterfaceList {
  public static final Class<?>[] LIST = {
    org.chromium.sdk.internal.wip.protocol.input.console.CallFrameValue.class,
    org.chromium.sdk.internal.wip.protocol.input.console.ConsoleMessageValue.class,
    org.chromium.sdk.internal.wip.protocol.input.console.MessageAddedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.console.MessageRepeatCountUpdatedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.console.MessagesClearedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.BreakpointResolvedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.CallFrameValue.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.CanSetScriptSourceData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.CausesRecompilationData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.CompileScriptData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.EvaluateOnCallFrameData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.FunctionDetailsValue.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.GetFunctionDetailsData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.GetScriptSourceData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.GlobalObjectClearedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.LocationValue.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.PausedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.PausedEventData.Data.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.RestartFrameData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.RestartFrameData.Result.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.ResumedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.RunScriptData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.ScopeValue.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.ScriptFailedToParseEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.ScriptParsedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.SearchInContentData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.SetBreakpointByUrlData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.SetBreakpointData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.SetScriptSourceData.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.SetScriptSourceData.Result.class,
    org.chromium.sdk.internal.wip.protocol.input.debugger.SupportsSeparateScriptCompilationAndExecutionData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.AttributeModifiedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.AttributeRemovedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.CharacterDataModifiedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.ChildNodeCountUpdatedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.ChildNodeInsertedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.ChildNodeRemovedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.DocumentUpdatedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.EventListenerValue.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.GetAttributesData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.GetDocumentData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.GetEventListenersForNodeData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.GetOuterHTMLData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.GetSearchResultsData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.InlineStyleInvalidatedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.MoveToData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.NodeValue.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.PerformSearchData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.PushNodeByPathToFrontendData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.QuerySelectorAllData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.QuerySelectorData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.RequestNodeData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.ResolveNodeData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.SetChildNodesEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.SetNodeNameData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.ShadowRootPoppedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.dom.ShadowRootPushedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.CachedResourceValue.class,
    org.chromium.sdk.internal.wip.protocol.input.network.CanClearBrowserCacheData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.CanClearBrowserCookiesData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.DataReceivedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.GetResponseBodyData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.HeadersValue.class,
    org.chromium.sdk.internal.wip.protocol.input.network.InitiatorValue.class,
    org.chromium.sdk.internal.wip.protocol.input.network.LoadingFailedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.LoadingFinishedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.RequestServedFromCacheEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.RequestServedFromMemoryCacheEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.RequestValue.class,
    org.chromium.sdk.internal.wip.protocol.input.network.RequestWillBeSentEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.ResourceTimingValue.class,
    org.chromium.sdk.internal.wip.protocol.input.network.ResponseReceivedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.ResponseValue.class,
    org.chromium.sdk.internal.wip.protocol.input.network.WebSocketClosedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.WebSocketCreatedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.WebSocketFrameErrorEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.WebSocketFrameReceivedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.WebSocketFrameSentEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.WebSocketFrameValue.class,
    org.chromium.sdk.internal.wip.protocol.input.network.WebSocketHandshakeResponseReceivedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.network.WebSocketRequestValue.class,
    org.chromium.sdk.internal.wip.protocol.input.network.WebSocketResponseValue.class,
    org.chromium.sdk.internal.wip.protocol.input.network.WebSocketWillSendHandshakeRequestEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.AddScriptToEvaluateOnLoadData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.CanOverrideDeviceMetricsData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.CanOverrideDeviceOrientationData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.CanOverrideGeolocationData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.CanShowFPSCounterData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.CookieValue.class,
    org.chromium.sdk.internal.wip.protocol.input.page.DomContentEventFiredEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.FrameDetachedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.FrameNavigatedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.FrameResourceTreeValue.class,
    org.chromium.sdk.internal.wip.protocol.input.page.FrameResourceTreeValue.Resources.class,
    org.chromium.sdk.internal.wip.protocol.input.page.FrameValue.class,
    org.chromium.sdk.internal.wip.protocol.input.page.GetCompositingBordersVisibleData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.GetCookiesData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.GetResourceContentData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.GetResourceTreeData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.GetScriptExecutionStatusData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.LoadEventFiredEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.SearchInResourceData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.SearchInResourcesData.class,
    org.chromium.sdk.internal.wip.protocol.input.page.SearchMatchValue.class,
    org.chromium.sdk.internal.wip.protocol.input.page.SearchResultValue.class,
    org.chromium.sdk.internal.wip.protocol.input.runtime.CallFunctionOnData.class,
    org.chromium.sdk.internal.wip.protocol.input.runtime.EvaluateData.class,
    org.chromium.sdk.internal.wip.protocol.input.runtime.ExecutionContextCreatedEventData.class,
    org.chromium.sdk.internal.wip.protocol.input.runtime.ExecutionContextDescriptionValue.class,
    org.chromium.sdk.internal.wip.protocol.input.runtime.GetPropertiesData.class,
    org.chromium.sdk.internal.wip.protocol.input.runtime.InternalPropertyDescriptorValue.class,
    org.chromium.sdk.internal.wip.protocol.input.runtime.ObjectPreviewValue.class,
    org.chromium.sdk.internal.wip.protocol.input.runtime.PropertyDescriptorValue.class,
    org.chromium.sdk.internal.wip.protocol.input.runtime.PropertyPreviewValue.class,
    org.chromium.sdk.internal.wip.protocol.input.runtime.RemoteObjectValue.class,
  };
}
