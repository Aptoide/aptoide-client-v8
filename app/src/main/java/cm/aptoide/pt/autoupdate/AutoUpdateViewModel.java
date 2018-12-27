package cm.aptoide.pt.autoupdate;

public class AutoUpdateViewModel {

  private final int versionCode;
  private final String uri;
  private final String md5;
  private final String minSdk;
  private final String packageName;
  private final boolean loading;
  private final Error error;
  private final boolean shouldUpdate;

  public AutoUpdateViewModel(int versionCode, String uri, String md5, String minSdk,
      String packageName, boolean shouldUpdate) {
    this.versionCode = versionCode;
    this.uri = uri;
    this.md5 = md5;
    this.minSdk = minSdk;
    this.packageName = packageName;
    this.shouldUpdate = shouldUpdate;
    loading = false;
    error = null;
  }

  public AutoUpdateViewModel(AutoUpdateViewModel autoUpdateViewModel, boolean shouldUpdate) {
    this.versionCode = autoUpdateViewModel.getVersionCode();
    this.uri = autoUpdateViewModel.getUri();
    this.md5 = autoUpdateViewModel.getMd5();
    this.minSdk = autoUpdateViewModel.getMinSdk();
    this.packageName = autoUpdateViewModel.getPackageName();
    this.shouldUpdate = shouldUpdate;
    loading = false;
    error = null;
  }

  public AutoUpdateViewModel(Error error) {
    this.error = error;
    versionCode = -1;
    uri = null;
    md5 = null;
    minSdk = null;
    packageName = null;
    shouldUpdate = false;
    loading = false;
  }

  public AutoUpdateViewModel(boolean loading) {
    this.loading = loading;
    versionCode = -1;
    uri = null;
    md5 = null;
    minSdk = null;
    packageName = null;
    shouldUpdate = false;
    error = null;
  }

  public int getVersionCode() {
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

  public boolean hasError() {
    return error != null;
  }

  public String getPackageName() {
    return packageName;
  }

  public enum Error {
    NETWORK, GENERIC
  }
}
