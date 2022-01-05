package cm.aptoide.pt.autoupdate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AutoUpdateJsonResponse {

  private int versioncode;
  private String uri;
  private String md5;
  @JsonProperty("minSdk") private String minSdk;

  public AutoUpdateJsonResponse() {

  }

  public int getVersioncode() {
    return versioncode;
  }

  public void setVersioncode(int versioncode) {
    this.versioncode = versioncode;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public String getMinSdk() {
    return minSdk;
  }

  public void setMinSdk(String minSdk) {
    this.minSdk = minSdk;
  }
}
