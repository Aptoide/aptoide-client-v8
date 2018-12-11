package cm.aptoide.pt.view;

public class AutoUpdateModel {

  private final long versionCode;
  private final String uri;
  private final String md5;
  private final String minSdk;
  private final boolean loading;
  private final Error error;

  public AutoUpdateModel(long versionCode, String uri, String md5, String minSdk) {
    this.versionCode = versionCode;
    this.uri = uri;
    this.md5 = md5;
    this.minSdk = minSdk;
    loading = false;
    error = null;
  }

  public AutoUpdateModel(Error error) {
    this.error = error;
    versionCode = -1;
    uri = null;
    md5 = null;
    minSdk = null;
    loading = false;
  }

  public AutoUpdateModel(boolean loading) {
    this.loading = loading;
    versionCode = -1;
    uri = null;
    md5 = null;
    minSdk = null;
    error = null;
  }

  public long getVersionCode() {
    return versionCode;
  }

  public String getUri() {
    return uri;
  }

  public String getMd5() {
    return md5;
  }

  public String getMinSdk() {
    return minSdk;
  }

  public boolean isLoading() {
    return loading;
  }

  public Error getError() {
    return error;
  }

  public enum Error {
    NETWORK, GENERIC
  }
}
