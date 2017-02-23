/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/07/2016.
 */

package cm.aptoide.pt;

import android.support.multidex.MultiDex;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created by neuro on 10-05-2016.
 */
public class AptoideBase extends V8Engine {

  @Override public void onCreate() {
    setupCrashReports(BuildConfig.CRASH_REPORTS_DISABLED);
    MultiDex.install(this);
    super.onCreate();
  }

  @Override public AptoidePreferencesConfiguration createConfiguration() {
    return new VanillaConfiguration();
  }
}
