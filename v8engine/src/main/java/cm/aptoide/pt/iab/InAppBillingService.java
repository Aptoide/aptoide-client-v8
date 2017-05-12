/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.iab;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.billing.inapp.BillingBinder;
import cm.aptoide.pt.v8engine.billing.repository.ProductFactory;
import cm.aptoide.pt.v8engine.billing.view.PaymentThrowableCodeMapper;

public class InAppBillingService extends Service {

  private AptoideInAppBillingService.Stub billingBinder;

  @Override public void onCreate() {
    super.onCreate();
    billingBinder =
        new BillingBinder(this, ((V8Engine) getApplicationContext()).getInAppBillingRepository(),
            ((V8Engine) getApplicationContext()).getInAppBillingSerializer(),
            new PaymentThrowableCodeMapper(), new PaymentThrowableCodeMapper(),
            new ProductFactory(), ((V8Engine) getApplicationContext()).getAccountManager(),
            ((V8Engine) getApplicationContext()).getAptoideBilling());
  }

  @Override public IBinder onBind(Intent intent) {
    return billingBinder;
  }
}