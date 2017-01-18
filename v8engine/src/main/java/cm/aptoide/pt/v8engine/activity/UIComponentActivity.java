/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.crashreports.CrashlyticsCrashLogger;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.interfaces.UiComponent;
import cm.aptoide.pt.v8engine.view.PermissionServiceActivity;
import lombok.Getter;

/**
 * Created by neuro on 01-05-2016.
 */
public abstract class UIComponentActivity extends PermissionServiceActivity
    implements UiComponent {

  private static final String TAG = UIComponentActivity.class.getName();
  @Getter private boolean _resumed = false;

  @Override protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    // https://fabric.io/downloads/gradle/ndk
    // Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
      ((CrashlyticsCrashLogger) CrashReport.getInstance()
          .getLogger(CrashlyticsCrashLogger.class)).setLanguage(
          getResources().getConfiguration().locale.getLanguage());
    } else {
      ((CrashlyticsCrashLogger) CrashReport.getInstance()
          .getLogger(CrashlyticsCrashLogger.class)).setLanguage(
          getResources().getConfiguration().getLocales().get(0).getLanguage());
    }

    setUpAnalytics();

    if (getIntent().getExtras() != null) {
      loadExtras(getIntent().getExtras());
    }
    setContentView(getContentViewId());
    bindViews(getWindow().getDecorView().getRootView());
    setupToolbar();
    setupViews();
  }

  @Override protected void onStop() {
    super.onStop();
    Analytics.Lifecycle.Activity.onStop(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  private void setUpAnalytics() {
    Analytics.Dimensions.setPartnerDimension(Analytics.Dimensions.PARTNER);
    Analytics.Dimensions.setVerticalDimension(Analytics.Dimensions.VERTICAL);
    Analytics.Dimensions.setGmsPresent(
        DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(this));
  }

  /**
   * @return the LayoutRes to be set on {@link #setContentView(int)}.
   */
  @LayoutRes public abstract int getContentViewId();

  @Override protected void onPause() {
    super.onPause();
    _resumed = false;
    Analytics.Lifecycle.Activity.onPause(this);
  }

  @Override protected void onResume() {
    super.onResume();
    _resumed = true;
    Analytics.Lifecycle.Activity.onResume(this);
  }

  @Override protected void onStart() {
    super.onStart();
    Analytics.Lifecycle.Activity.onStart(this);
  }

  /**
   * @return o nome so monitor associado a esta activity, para efeitos de Analytics.
   */
  protected abstract String getAnalyticsScreenName();

}
