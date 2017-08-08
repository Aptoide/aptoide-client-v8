/*
 * Copyright (c) 2016.
 * Modified on 01/07/2016.
 */

package cm.aptoide.pt;

import android.content.Context;
import android.support.multidex.MultiDex;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;

/**
 * Created by neuro on 10-05-2016.
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

  @Override public AptoidePreferencesConfiguration createConfiguration() {
    return new VanillaConfiguration(getDefaultSharedPreferences(), getBaseContext());
  }
}
