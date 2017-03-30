/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.iab;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;

public class InAppBillingService extends Service {

  private AptoideInAppBillingService.Stub billingBinder;

  @Override public void onCreate() {
    super.onCreate();
    billingBinder = new BillingBinder(this, RepositoryFactory.getInAppBillingRepository(this),
        new InAppBillingSerializer(), new ErrorCodeFactory(), new PurchaseErrorCodeFactory(),
        new ProductFactory(), ((V8Engine) getApplicationContext()).getAccountManager());
  }

  @Override public IBinder onBind(Intent intent) {
    return billingBinder;
  }
}