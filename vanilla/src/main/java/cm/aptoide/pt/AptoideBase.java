/*
 * Copyright (c) 2016.
 * Modified on 01/07/2016.
 */

package cm.aptoide.pt;

import android.content.Context;
import android.support.multidex.MultiDex;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.v8engine.V8Engine;
import com.appsflyer.AppsFlyerLib;

/**
 * Created by neuro on 10-05-2016.
 */
public class AptoideBase extends V8Engine {

  @Override public void onCreate() {
    setupCrashReports(BuildConfig.CRASH_REPORTS_DISABLED);
    super.onCreate();
    //setupStrictMode();

    /*
     * AppsFlyer
     */
    AppsFlyerLib.getInstance()
        .setAndroidIdData(((V8Engine) getApplicationContext()).getIdsRepository()
            .getUniqueIdentifier());

    AppsFlyerLib.getInstance()
        .startTracking(this, getString(R.string.APPS_FLYER_DEV_KEY));
    AppsFlyerLib.getInstance()
        .reportTrackSession(this);
  }

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  @Override public AptoidePreferencesConfiguration createConfiguration() {
    return new VanillaConfiguration();
  }
}
