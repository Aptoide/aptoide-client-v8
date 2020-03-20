package cm.aptoide.pt.dataprovider.aab;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AppBundlesVisibilityManagerTest {

  @Test public void shouldEnableAppBundles_MIUI_CompatibleAppsOnly() {
    AppBundlesVisibilityManager appBundlesVisibilityManager =
        new AppBundlesVisibilityManager(true, () -> true);

    assertFalse(appBundlesVisibilityManager.shouldEnableAppBundles());
  }

  @Test public void shouldEnableAppBundles_MIUI_ShowNonCompatibleApps() {
    AppBundlesVisibilityManager appBundlesVisibilityManager =
        new AppBundlesVisibilityManager(true, () -> false);

    assertTrue(appBundlesVisibilityManager.shouldEnableAppBundles());
  }

  @Test public void shouldEnableAppBundles_NonMIUI_ShowNonCompatibleApps() {
    AppBundlesVisibilityManager appBundlesVisibilityManager =
        new AppBundlesVisibilityManager(false, () -> false);

    assertTrue(appBundlesVisibilityManager.shouldEnableAppBundles());
  }

  @Test public void shouldEnableAppBundles_NonMIUI_CompatibleAppsOnly() {
    AppBundlesVisibilityManager appBundlesVisibilityManager =
        new AppBundlesVisibilityManager(false, () -> true);

    assertTrue(appBundlesVisibilityManager.shouldEnableAppBundles());
  }
}