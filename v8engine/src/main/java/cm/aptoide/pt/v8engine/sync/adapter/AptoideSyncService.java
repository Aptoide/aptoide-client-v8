/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync.adapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.V8Engine;

public class AptoideSyncService extends Service {

  private static final Object lock = new Object();
  private static AptoideSyncAdapter syncAdapter;

  @Override public void onCreate() {
    super.onCreate();
    synchronized (lock) {
      if (syncAdapter == null) {
        syncAdapter =
            new AptoideSyncAdapter(getApplicationContext(), true, false, new ProductBundleMapper(),
                ((V8Engine) getApplicationContext()).getBillingAnalytics(),
                ((V8Engine) getApplicationContext()).getAccountPayer(),
                ((V8Engine) getApplicationContext()).getV3TransactionService(),
                ((V8Engine) getApplicationContext()).getRealmTransactionPersistence(),
                ((V8Engine) getApplicationContext()).getV3AuthorizationService(),
                ((V8Engine) getApplicationContext()).getRealmAuthorizationPersistence());
      }
    }
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return syncAdapter.getSyncAdapterBinder();
  }
}
