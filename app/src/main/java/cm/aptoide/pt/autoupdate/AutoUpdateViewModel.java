package cm.aptoide.pt.autoupdate;

public class AutoUpdateViewModel {

  private final long versionCode;
  private final String uri;
  private final String md5;
  private final String minSdk;
  private final boolean loading;
  private final Error error;
  private boolean shouldUpdate;

  public AutoUpdateViewModel(long versionCode, String uri, String md5, String minSdk) {
    this.versionCode = versionCode;
    this.uri = uri;
    this.md5 = md5;
    this.minSdk = minSdk;
    shouldUpdate = false;
    loading = false;
    error = null;
  }

  public AutoUpdateViewModel(Error error) {
    this.error = error;
    versionCode = -1;
    uri = null;
    md5 = null;
    minSdk = null;
    shouldUpdate = false;
    loading = false;
  }

  public AutoUpdateViewModel(boolean loading) {
    this.loading = loading;
    versionCode = -1;
    uri = null;
    md5 = null;
    minSdk = null;
    shouldUpdate = false;
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

  public boolean shouldUpdate() {
    return shouldUpdate;
  }

  public void setShouldUpdate(boolean shouldUpdate) {
    this.shouldUpdate = shouldUpdate;
  }

  public enum Error {
    NETWORK, GENERIC
  }
}
