/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.crashreports.CrashlyticsCrashLogger;
import cm.aptoide.pt.v8engine.view.permission.PermissionServiceActivity;
import lombok.Getter;

/**
 * Created by neuro on 01-05-2016.
 */
public abstract class BaseActivity extends PermissionServiceActivity {

  @Getter private boolean _resumed = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Analytics.Lifecycle.Activity.onCreate(this);
    // https://fabric.io/downloads/gradle/ndk
    // Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
      ((CrashlyticsCrashLogger) CrashReport.getInstance()
          .getLogger(CrashlyticsCrashLogger.class)).setLanguage(
          getResources().getConfiguration().locale.getLanguage());
    } else {
      ((CrashlyticsCrashLogger) CrashReport.getInstance()
          .getLogger(CrashlyticsCrashLogger.class)).setLanguage(getResources().getConfiguration()
          .getLocales()
          .get(0)
          .getLanguage());
    }

    setUpAnalytics();
  }

  private void setUpAnalytics() {
    Analytics.Dimensions.setPartnerDimension(Analytics.Dimensions.PARTNER);
    Analytics.Dimensions.setVerticalDimension(Analytics.Dimensions.VERTICAL);
    Analytics.Dimensions.setGmsPresent(
        DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(this));
  }

  @Override protected void onStart() {
    super.onStart();
    Analytics.Lifecycle.Activity.onStart(this);
  }

  @Override protected void onResume() {
    super.onResume();
    _resumed = true;
    Analytics.Lifecycle.Activity.onResume(this);
  }

  @Override protected void onPause() {
    super.onPause();
    _resumed = false;
    Analytics.Lifecycle.Activity.onPause(this);
  }

  @Override protected void onStop() {
    super.onStop();
    Analytics.Lifecycle.Activity.onStop(this);
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Analytics.Lifecycle.Activity.onNewIntent(this, intent);
  }
}
