/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.iab;

import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;

public class InAppBillingService extends Service {

  private AptoideInAppBillingService.Stub billingBinder;

  @Override public void onCreate() {
    super.onCreate();
    billingBinder = new BillingBinder(this, RepositoryFactory.getInAppBillingRepository(this),
        new InAppBillingSerializer(), new ErrorCodeFactory(), new PurchaseErrorCodeFactory(),
        new ProductFactory(), AptoideAccountManager.getInstance(this,
        Application.getConfiguration(), new SecureCoderDecoder.Builder(this.getApplicationContext()).create(),
        AccountManager.get(this.getApplicationContext()), new IdsRepositoryImpl(
            SecurePreferencesImplementation.getInstance(),
            this.getApplicationContext())));
  }

  @Override public IBinder onBind(Intent intent) {
    return billingBinder;
  }
}