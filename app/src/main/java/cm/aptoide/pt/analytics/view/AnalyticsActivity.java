/*
 * Copyright (c) 2016.
 * Modified on 29/08/2016.
 */

package cm.aptoide.pt.analytics.view;

import android.os.Build;
import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.crashreports.CrashlyticsCrashLogger;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.permission.PermissionProviderActivity;

public abstract class AnalyticsActivity extends PermissionProviderActivity {

  private boolean _resumed = false;

  public boolean is_resumed() {
    return _resumed;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Analytics.Lifecycle.Activity.onCreate(this);

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

    Analytics.Dimensions.setGmsPresent(AdNetworkUtils.isGooglePlayServicesAvailable(this));
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
  }

  @Override protected void onStop() {
    super.onStop();
    Analytics.Lifecycle.Activity.onStop(this);
  }
}
