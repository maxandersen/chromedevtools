// Generated source.
// Generator: org.chromium.sdk.internal.wip.tools.protocolgenerator.Generator
// Origin: http://src.chromium.org/blink/branches/chromium/2490/Source/devtools/protocol.json@<unknown>

package org.chromium.sdk.internal.wip.protocol.output.debugger;

/**
Returns detailed information on given function.
 */
public class GetFunctionDetailsParams extends org.chromium.sdk.internal.wip.protocol.output.WipParamsWithResponse<org.chromium.sdk.internal.wip.protocol.input.debugger.GetFunctionDetailsData> {
  /**
   @param functionId Id of the function to get details for.
   */
  public GetFunctionDetailsParams(String/*See org.chromium.sdk.internal.wip.protocol.common.runtime.RemoteObjectIdTypedef*/ functionId) {
    this.put("functionId", functionId);
  }

  public static final String METHOD_NAME = org.chromium.sdk.internal.wip.protocol.BasicConstants.Domain.DEBUGGER + ".getFunctionDetails";

  @Override protected String getRequestName() {
    return METHOD_NAME;
  }

  @Override public org.chromium.sdk.internal.wip.protocol.input.debugger.GetFunctionDetailsData parseResponse(org.chromium.sdk.internal.wip.protocol.input.WipCommandResponse.Data data, org.chromium.sdk.internal.wip.protocol.input.WipGeneratedParserRoot parser) throws org.chromium.sdk.internal.protocolparser.JsonProtocolParseException {
    return parser.parseDebuggerGetFunctionDetailsData(data.getUnderlyingObject());
  }

}