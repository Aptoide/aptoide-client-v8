package cm.aptoide.pt.spotandshareapp;

import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created by filipe on 27-06-2017.
 */

public class SpotAndShareApplication extends V8Engine {

  @Override public void onCreate() {
    setupCrashReports(BuildConfig.CRASH_REPORTS_DISABLED);
    super.onCreate();
  }

  @Override public AptoidePreferencesConfiguration createConfiguration() {
    return new VanillaConfiguration(getDefaultSharedPreferences());
  }
}
