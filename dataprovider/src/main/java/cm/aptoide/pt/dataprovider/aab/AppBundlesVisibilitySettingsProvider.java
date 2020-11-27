package cm.aptoide.pt.dataprovider.aab;

import android.content.SharedPreferences;
import cm.aptoide.pt.preferences.managed.ManagedKeys;

public class AppBundlesVisibilitySettingsProvider implements SettingsValuesProvider {
  private final SharedPreferences sharedPreferences;

  public AppBundlesVisibilitySettingsProvider(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @Override public boolean isOnlyShowCompatibleApps() {
    return sharedPreferences.getBoolean(ManagedKeys.HWSPECS_FILTER, true);
  }

  @Override public boolean isEnforceNativeInstaller() {
    return sharedPreferences.getBoolean(ManagedKeys.ENFORCE_NATIVE_INSTALLER_KEY, false);
  }
}
