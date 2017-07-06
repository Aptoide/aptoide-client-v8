package cm.aptoide.pt.v8engine.timeline.request;

public class RelatedAppsRequest extends BaseRequest {

  public RelatedAppsRequest(String accessToken, String appMd5Sum, String appPackage, String appVersionCode,
      String appUid, String cdn, String language, boolean mature, String q) {
    super(accessToken, appMd5Sum, appPackage, appVersionCode, appUid, cdn, language, mature, q);
  }
}
