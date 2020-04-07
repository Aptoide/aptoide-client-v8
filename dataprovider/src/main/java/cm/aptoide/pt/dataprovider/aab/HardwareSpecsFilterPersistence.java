package cm.aptoide.pt.dataprovider.aab;

import android.content.SharedPreferences;
import cm.aptoide.pt.preferences.managed.ManagedKeys;

public class HardwareSpecsFilterPersistence implements HardwareSpecsFilterProvider {
  private final SharedPreferences sharedPreferences;

  public HardwareSpecsFilterPersistence(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @Override public boolean isOnlyShowCompatibleApps() {
    return sharedPreferences.getBoolean(ManagedKeys.HWSPECS_FILTER, true);
  }
}
