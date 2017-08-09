/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.iab;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.billing.external.ExternalBillingBinder;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;

public class InAppBillingService extends Service {

  private AptoideInAppBillingService.Stub billingBinder;

  @Override public void onCreate() {
    super.onCreate();
    billingBinder = new ExternalBillingBinder(this,
        ((V8Engine) getApplicationContext()).getInAppBillingSerializer(),
        ((V8Engine) getApplicationContext()).getPaymentThrowableCodeMapper(),
        ((V8Engine) getApplicationContext()).getBilling(), CrashReport.getInstance(),
        ((V8Engine) getApplicationContext()).getBillingIdResolver(),
        BuildConfig.IN_BILLING_SUPPORTED_API_VERSION,
        ((V8Engine) getApplicationContext()).getBillingAnalytics());
  }

  @Override public IBinder onBind(Intent intent) {
    return billingBinder;
  }
}