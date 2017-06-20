/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.PaymentAuthorization;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.billing.repository.PaymentConfirmationFactory;
import cm.aptoide.pt.v8engine.billing.repository.sync.ProductBundleMapper;

/**
 * Created by marcelobenites on 18/11/16.
 */
public class AptoideSyncService extends Service {

  private static final Object lock = new Object();
  private static AptoideSyncAdapter syncAdapter;

  @Override public void onCreate() {
    super.onCreate();
    synchronized (lock) {
      if (syncAdapter == null) {
        syncAdapter = new AptoideSyncAdapter(getApplicationContext(), true, false,
            new PaymentConfirmationFactory(),
            ((V8Engine) getApplicationContext()).getAuthorizationFactory(),
            new ProductBundleMapper(), new NetworkOperatorManager(
            (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE)),
            AccessorFactory.getAccessorFor(PaymentConfirmation.class),
            AccessorFactory.getAccessorFor(PaymentAuthorization.class),
            ((V8Engine) getApplicationContext()).getBaseBodyInterceptorV3(),
            ((V8Engine) getApplicationContext()).getDefaultClient(),
            WebService.getDefaultConverter(),
            ((V8Engine) getApplicationContext()).getPaymentAnalytics(),
            ((V8Engine) getApplicationContext()).getAccountPayer(),
            ((V8Engine) getApplicationContext()).getTokenInvalidator(),
            ((V8Engine) getApplicationContext()).getDefaultSharedPreferences());
      }
    }
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return syncAdapter.getSyncAdapterBinder();
  }
}
