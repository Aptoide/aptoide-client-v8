package cm.aptoide.pt.autoupdate;

public class AutoUpdateJsonResponse {

  private long versioncode;
  private String uri;
  private String md5;
  private String minSdk;

  public AutoUpdateJsonResponse() {

  }

  public long getVersioncode() {
    return versioncode;
  }

  public void setVersioncode(long versioncode) {
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
