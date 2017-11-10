package cm.aptoide.pt.sync.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.sync.Sync;
import cm.aptoide.pt.sync.SyncScheduler;

public class AlarmSyncService extends Service {

  private SyncScheduler scheduler;
  private SyncStorage storage;
  private CrashReport crashReport;

  @Override public void onCreate() {
    super.onCreate();
    scheduler = ((AptoideApplication) getApplicationContext()).getAlarmSyncScheduler();
    storage = ((AptoideApplication) getApplicationContext()).getSyncStorage();
    crashReport = CrashReport.getInstance();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {

    if (intent != null && AlarmSyncScheduler.ACTION_SYNC.equals(intent.getAction())) {

      final String syncId = intent.getData()
          .getFragment();
      final Sync sync = storage.get(syncId);
      final boolean reschedule = intent.getBooleanExtra(AlarmSyncScheduler.EXTRA_RESCHEDULE, false);

      if (sync != null) {
        sync.execute()
            .doOnTerminate(() -> {
              if (reschedule) {
                scheduler.reschedule(sync);
              }
              stopSelf(startId);
            })
            .subscribe(() -> {
            }, throwable -> crashReport.log(throwable));
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
