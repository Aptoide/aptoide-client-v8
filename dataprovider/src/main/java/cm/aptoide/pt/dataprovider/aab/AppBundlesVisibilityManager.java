package cm.aptoide.pt.dataprovider.aab;

public class AppBundlesVisibilityManager {
  private final boolean isMIUIWithAABFix;
  private final boolean isDeviceMIUI;
  private final SettingsValuesProvider settingsValuesProvider;

  public AppBundlesVisibilityManager(boolean isMIUIWithAABFix, boolean isDeviceMIUI,
      SettingsValuesProvider SettingsValuesProvider) {
    this.isMIUIWithAABFix = isMIUIWithAABFix;
    this.isDeviceMIUI = isDeviceMIUI;
    this.settingsValuesProvider = SettingsValuesProvider;
  }

  public boolean shouldEnableAppBundles() {
    return !settingsValuesProvider.isEnforceNativeInstaller() && (!isDeviceMIUI
        || !settingsValuesProvider.isOnlyShowCompatibleApps()
        || (isDeviceMIUI && isMIUIWithAABFix));
  }
}
