package cm.aptoide.pt.v8engine.timeline.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostRequest extends BaseRequest {
  private String url;
  private String content;
  @JsonProperty("package_name") private String packageName;

  public PostRequest(String accessToken, String appMd5Sum, String appPackage, String appVersionCode,
      String appUid, String cdn, String language, boolean mature, String q) {
    super(accessToken, appMd5Sum, appPackage, appVersionCode, appUid, cdn, language, mature, q);
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }
}
