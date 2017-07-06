package cm.aptoide.pt.v8engine.timeline.request;

public class CardPreviewRequest extends BaseRequest {

  private final String url;

  public CardPreviewRequest(String accessToken, String appMd5Sum, String appPackage, String appVersionCode,
      String appUid, String cdn, String language, boolean mature, String q, String url) {
    super(accessToken, appMd5Sum, appPackage, appVersionCode, appUid, cdn, language, mature, q);
    this.url = url;
  }
}
