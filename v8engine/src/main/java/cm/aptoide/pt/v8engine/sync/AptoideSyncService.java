/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

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
        syncAdapter = new AptoideSyncAdapter(getApplicationContext(), true, false);
      }
    }
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return syncAdapter.getSyncAdapterBinder();
  }
}
