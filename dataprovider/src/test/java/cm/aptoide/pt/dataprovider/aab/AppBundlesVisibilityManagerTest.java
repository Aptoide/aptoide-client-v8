package cm.aptoide.pt.dataprovider.aab;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AppBundlesVisibilityManagerTest {

  @Test public void shouldEnableAppBundles_MIUI_CompatibleAppsOnly() {
    AppBundlesVisibilityManager appBundlesVisibilityManager =
        new AppBundlesVisibilityManager(false, true, getMockedSettingsValuesProvider(true, false));

    assertFalse(appBundlesVisibilityManager.shouldEnableAppBundles());
  }

  @Test public void shouldEnableAppBundles_MIUI_ShowNonCompatibleApps() {
    AppBundlesVisibilityManager appBundlesVisibilityManager =
        new AppBundlesVisibilityManager(false, true, getMockedSettingsValuesProvider(false, false));

    assertTrue(appBundlesVisibilityManager.shouldEnableAppBundles());
  }

  @Test public void shouldEnableAppBundles_NonMIUI_ShowNonCompatibleApps() {
    AppBundlesVisibilityManager appBundlesVisibilityManager =
        new AppBundlesVisibilityManager(false, false,
            getMockedSettingsValuesProvider(false, false));

    assertTrue(appBundlesVisibilityManager.shouldEnableAppBundles());
  }

  @Test public void shouldEnableAppBundles_NonMIUI_CompatibleAppsOnly() {
    AppBundlesVisibilityManager appBundlesVisibilityManager =
        new AppBundlesVisibilityManager(false, false, getMockedSettingsValuesProvider(true, false));

    assertTrue(appBundlesVisibilityManager.shouldEnableAppBundles());
  }

  @Test public void shouldEnableAppBundles_EnforceNativeInstaller() {
    AppBundlesVisibilityManager appBundlesVisibilityManager =
        new AppBundlesVisibilityManager(false, false, getMockedSettingsValuesProvider(true, true));

    assertFalse(appBundlesVisibilityManager.shouldEnableAppBundles());
  }

  private SettingsValuesProvider getMockedSettingsValuesProvider(boolean isOnlyShowCompatibleApps,
      boolean isEnforceNativeInstaller) {
    return new SettingsValuesProvider() {
      @Override public boolean isOnlyShowCompatibleApps() {
        return isOnlyShowCompatibleApps;
      }

      @Override public boolean isEnforceNativeInstaller() {
        return isEnforceNativeInstaller;
      }
    };
  }
}