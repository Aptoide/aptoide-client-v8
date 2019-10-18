package cm.aptoide.pt.dataprovider.aab;

public class AppBundlesVisibilityManager {
  private final boolean isDeviceMiui;

  public AppBundlesVisibilityManager(boolean isDeviceMiui) {
    this.isDeviceMiui = isDeviceMiui;
  }

  public boolean shouldEnableAppBundles() {
    return !isDeviceMiui;
  }
}
