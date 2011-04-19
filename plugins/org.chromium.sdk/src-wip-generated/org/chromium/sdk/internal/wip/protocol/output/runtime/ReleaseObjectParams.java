// Generated source.
// Generator: org.chromium.sdk.internal.wip.tools.protocolgenerator.Generator
// Origin: http://svn.webkit.org/repository/webkit/trunk/Source/WebCore/inspector/Inspector.json@84080

package org.chromium.sdk.internal.wip.protocol.output.runtime;

/**
Releases remote object with given id.
 */
public class ReleaseObjectParams extends org.chromium.sdk.internal.wip.protocol.output.WipParams {
  public static final String METHOD_NAME = org.chromium.sdk.internal.wip.protocol.BasicConstants.Domain.RUNTIME + ".releaseObject";

  /**
   @param objectId Identifier of the object to release.
   */
  public ReleaseObjectParams(String objectId) {
    this.put("objectId", objectId);
  }

  @Override protected String getRequestName() {
    return METHOD_NAME;
  }

}