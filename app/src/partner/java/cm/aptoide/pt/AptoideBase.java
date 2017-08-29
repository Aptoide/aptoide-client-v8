package cm.aptoide.pt;

import android.content.Context;
import android.support.multidex.MultiDex;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.remotebootconfig.BootConfigJSONUtils;

/**
 * Created by diogoloureiro on 11/08/2017.
 *
 * Aptoide Base implementations
 */

public class AptoideBase extends V8Engine {

  @Override public void onCreate() {
    setupCrashReports(BuildConfig.CRASH_REPORTS_DISABLED);
    super.onCreate();
    //setupStrictMode();
  }

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  /**
   * defines the app configuration, passing the shared preferences and the remote boot config
   */
  @Override public AptoidePreferencesConfiguration createConfiguration() {
    return new VanillaConfiguration(getDefaultSharedPreferences(),
        BootConfigJSONUtils.getSavedRemoteBootConfig(getBaseContext()).getData());
  }
}