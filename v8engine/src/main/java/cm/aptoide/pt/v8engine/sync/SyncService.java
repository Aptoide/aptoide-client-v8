package cm.aptoide.pt.v8engine.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.V8Engine;

public class SyncService extends Service {

  private SyncScheduler scheduler;
  private SyncStorage storage;

  @Override public void onCreate() {
    super.onCreate();
    scheduler = ((V8Engine) getApplicationContext()).getSyncScheduler();
    storage = ((V8Engine) getApplicationContext()).getSyncStorage();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {

    if (intent != null && SyncScheduler.ACTION_SYNC.equals(intent.getAction())) {

      final String syncId = intent.getData()
          .getFragment();
      final Sync sync = storage.get(syncId);
      final boolean reschedule = intent.getBooleanExtra(SyncScheduler.EXTRA_RESCHEDULE, false);

      if (sync != null) {
        sync.execute()
            .doOnTerminate(() -> {
              if (reschedule) {
                scheduler.cancel(syncId);
                scheduler.schedule(sync);
              }
              stopSelf(startId);
            })
            .subscribe(() -> {
            }, throwable -> throwable.printStackTrace());
      } else {
        scheduler.cancel(syncId);
      }
    }

    return Service.START_REDELIVER_INTENT;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }
}
