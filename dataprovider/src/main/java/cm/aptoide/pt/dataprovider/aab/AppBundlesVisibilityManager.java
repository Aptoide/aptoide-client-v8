package cm.aptoide.pt.dataprovider.aab;

import android.content.SharedPreferences;
import cm.aptoide.pt.preferences.managed.ManagedKeys;

public class AppBundlesVisibilityManager {
  private final boolean isDeviceMiui;
  private final SharedPreferences sharedPreferences;

  public AppBundlesVisibilityManager(boolean isDeviceMiui, SharedPreferences sharedPreferences) {
    this.isDeviceMiui = isDeviceMiui;
    this.sharedPreferences = sharedPreferences;
  }

  public boolean shouldEnableAppBundles() {
    boolean showCompatibleAppsOnly = sharedPreferences.getBoolean(ManagedKeys.HWSPECS_FILTER, true);
    return !isDeviceMiui || !showCompatibleAppsOnly;
  }
}
