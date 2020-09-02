package cm.aptoide.pt.sync.alarm;

import android.content.Context;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import cm.aptoide.pt.notification.CampaignNotificationWorker;
import cm.aptoide.pt.sync.Sync;
import cm.aptoide.pt.sync.SyncScheduler;
import java.util.concurrent.TimeUnit;

public class AlarmSyncScheduler implements SyncScheduler {

  public static final String ACTION_SYNC = "cm.aptoide.pt.sync.alarm.action.SYNC";
  public static final String EXTRA_RESCHEDULE = "cm.aptoide.pt.sync.alarm.extra.RESCHEDULE";

  private final Context context;
  private final SyncStorage syncStorage;

  public AlarmSyncScheduler(Context context, SyncStorage syncStorage) {
    this.context = context;
    this.syncStorage = syncStorage;
  }

  @Override public void schedule(Sync sync) {
    if (sync.isPeriodic()) {
      schedulePeriodicSync(sync);
    } else {
      scheduleOneOffSync(sync);
    }
  }

  @Override public void cancel(String syncId) {
    WorkManager.getInstance(context)
        .cancelUniqueWork(syncId);
    syncStorage.remove(syncId);
  }

  @Override public void reschedule(Sync sync) {
    schedule(sync);
  }

  private void scheduleOneOffSync(Sync sync) {
    syncStorage.save(sync);
    setOneOffWorker(sync);
  }

  private void schedulePeriodicSync(Sync sync) {
    syncStorage.save(sync);
    setPeriodicWorker(sync);
  }

  private void setPeriodicWorker(Sync sync) {

    Data data = new Data.Builder().putString("sync_id", sync.getId())
        .putString("action", ACTION_SYNC)
        .putBoolean(EXTRA_RESCHEDULE, false)
        .build();

    PeriodicWorkRequest updatesWorkRequest =
        new PeriodicWorkRequest.Builder(CampaignNotificationWorker.class, sync.getInterval(),
            TimeUnit.MILLISECONDS).setInputData(data)
            .build();

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(sync.getId(), ExistingPeriodicWorkPolicy.KEEP,
            updatesWorkRequest);
  }

  private void setOneOffWorker(Sync sync) {

    Data data = new Data.Builder().putString("sync_id", sync.getId())
        .putString("action", ACTION_SYNC)
        .putBoolean(EXTRA_RESCHEDULE, false)
        .build();

    OneTimeWorkRequest workRequest =
        new OneTimeWorkRequest.Builder(CampaignNotificationWorker.class).setInputData(data)
            .build();

    WorkManager.getInstance(context)
        .enqueueUniqueWork(sync.getId(), ExistingWorkPolicy.REPLACE, workRequest);
  }
}
