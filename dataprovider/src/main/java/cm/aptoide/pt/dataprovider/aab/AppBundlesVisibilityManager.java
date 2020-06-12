package cm.aptoide.pt.dataprovider.aab;

public class AppBundlesVisibilityManager {
  private final boolean isMIUIWithAABFix;
  private boolean isDeviceMIUI;
  private final HardwareSpecsFilterProvider hardwareSpecsFilterProvider;

  public AppBundlesVisibilityManager(boolean isMIUIWithAABFix, boolean isDeviceMIUI,
      HardwareSpecsFilterProvider hardwareSpecsFilterProvider) {
    this.isMIUIWithAABFix = isMIUIWithAABFix;
    this.isDeviceMIUI = isDeviceMIUI;
    this.hardwareSpecsFilterProvider = hardwareSpecsFilterProvider;
  }

  public boolean shouldEnableAppBundles() {
    return !isDeviceMIUI || !hardwareSpecsFilterProvider.isOnlyShowCompatibleApps() || (isDeviceMIUI
        && isMIUIWithAABFix);
  }
}
