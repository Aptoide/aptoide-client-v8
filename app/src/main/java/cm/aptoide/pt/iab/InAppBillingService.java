/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.iab;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.billing.external.ExternalBillingBinder;
import cm.aptoide.pt.crashreports.CrashReport;

public class InAppBillingService extends Service {

  private AptoideInAppBillingService.Stub billingBinder;

  @Override public void onCreate() {
    super.onCreate();
    billingBinder = new ExternalBillingBinder(this,
        ((AptoideApplication) getApplicationContext()).getInAppBillingSerializer(),
        ((AptoideApplication) getApplicationContext()).getPaymentThrowableCodeMapper(),
        ((AptoideApplication) getApplicationContext()).getBilling(), CrashReport.getInstance(),
        ((AptoideApplication) getApplicationContext()).getBillingIdResolver(),
        BuildConfig.IN_BILLING_SUPPORTED_API_VERSION,
        ((AptoideApplication) getApplicationContext()).getBillingAnalytics());
  }

  @Override public IBinder onBind(Intent intent) {
    return billingBinder;
  }
}