package cm.aptoide.pt.dataprovider.aab;

public class AppBundlesVisibilityManager {
  private final boolean isDeviceMiui;
  private final HardwareSpecsFilterProvider hardwareSpecsFilterProvider;

  public AppBundlesVisibilityManager(boolean isDeviceMiui,
      HardwareSpecsFilterProvider hardwareSpecsFilterProvider) {
    this.isDeviceMiui = isDeviceMiui;
    this.hardwareSpecsFilterProvider = hardwareSpecsFilterProvider;
  }

  public boolean shouldEnableAppBundles() {
    return !isDeviceMiui || !hardwareSpecsFilterProvider.isOnlyShowCompatibleApps();
  }
}
