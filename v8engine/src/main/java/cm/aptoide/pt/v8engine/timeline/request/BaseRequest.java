package cm.aptoide.pt.v8engine.timeline.request;

import com.fasterxml.jackson.annotation.JsonProperty;

abstract class BaseRequest {

  @JsonProperty("access_token") private final String accessToken;
  @JsonProperty("aptoide_md5sum") private final String appMd5Sum;
  @JsonProperty("aptoide_package") private final String appPackage;
  @JsonProperty("aptoide_vercode") private final String appVersionCode;
  @JsonProperty("aptoide_uid") private final String appUid;
  private final String cdn;
  @JsonProperty("lang") private final String language;
  private final boolean mature;
  private final String q;

  BaseRequest(String accessToken, String appMd5Sum, String appPackage, String appVersionCode,
      String appUid, String cdn, String language, boolean mature, String q) {
    this.accessToken = accessToken;
    this.appMd5Sum = appMd5Sum;
    this.appPackage = appPackage;
    this.appVersionCode = appVersionCode;
    this.appUid = appUid;
    this.cdn = cdn;
    this.language = language;
    this.mature = mature;
    this.q = q;
  }
}
