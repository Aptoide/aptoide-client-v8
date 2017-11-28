package cm.aptoide.pt.sync.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import cm.aptoide.pt.sync.Sync;
import cm.aptoide.pt.sync.SyncScheduler;

public class AlarmSyncScheduler implements SyncScheduler {

  public static final String ACTION_SYNC = "cm.aptoide.pt.sync.alarm.action.SYNC";
  public static final String EXTRA_RESCHEDULE = "cm.aptoide.pt.sync.alarm.extra.RESCHEDULE";

  private static final String URI_SCHEME = "aptoide";
  private static final String URI_SCHEME_SPECIFIC_PART = "cm.aptoide.pt.sync.alarm";

  private final Context context;
  private final Class<?> serviceClass;
  private final AlarmManager alarmManager;
  private final SyncStorage syncStorage;

  public AlarmSyncScheduler(Context context, Class<?> serviceClass, AlarmManager alarmManager,
      SyncStorage syncStorage) {
    this.context = context;
    this.serviceClass = serviceClass;
    this.alarmManager = alarmManager;
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
    final PendingIntent pendingIntent = getPendingIntent(buildIntent(syncId, false));
    alarmManager.cancel(pendingIntent);
    pendingIntent.cancel();
    syncStorage.remove(syncId);
  }

  @Override public void reschedule(Sync sync) {
    if (isSyncScheduled(sync.getId())) {
      cancel(sync.getId());
      schedule(sync);
    }
  }

  private void scheduleOneOffSync(Sync sync) {
    if (sync.isExact()) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,
            getElapsedRealtimeTrigger(sync.getTrigger()),
            getPendingIntent(buildIntent(sync.getId(), false)));
      } else {
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME,
            getElapsedRealtimeTrigger(sync.getTrigger()),
            getPendingIntent(buildIntent(sync.getId(), false)));
      }
    } else {
      alarmManager.set(AlarmManager.ELAPSED_REALTIME, getElapsedRealtimeTrigger(sync.getTrigger()),
          getPendingIntent(buildIntent(sync.getId(), false)));
    }
    syncStorage.save(sync);
  }

  private void schedulePeriodicSync(Sync sync) {

    if (sync.isExact()) {
      scheduleExactPeriodicSync(sync);
    } else {
      scheduleInexactPeriodicSync(sync);
    }
  }

  private void scheduleInexactPeriodicSync(Sync sync) {
    if (isSyncScheduled(sync.getId())) {
      return;
    }

    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
        getElapsedRealtimeTrigger(sync.getTrigger()), sync.getInterval(),
        getPendingIntent(buildIntent(sync.getId(), false)));
    syncStorage.save(sync);
  }

  private void scheduleExactPeriodicSync(Sync sync) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {

      if (isSyncScheduled(sync.getId())) {
        return;
      }

      alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
          getElapsedRealtimeTrigger(sync.getTrigger()), sync.getInterval(),
          getPendingIntent(buildIntent(sync.getId(), false)));
      syncStorage.save(sync);
    } else {

      long trigger = 0;
      if (sync instanceof OneOffSyncWrapper) {
        trigger = sync.getTrigger();
        syncStorage.save(sync);
      } else {
        syncStorage.save(new OneOffSyncWrapper(sync, sync.getInterval()));
      }

      alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, getElapsedRealtimeTrigger(trigger),
          getPendingIntent(buildIntent(sync.getId(), true)));
    }
  }

  private long getElapsedRealtimeTrigger(long trigger) {
    return SystemClock.elapsedRealtime() + trigger;
  }

  private boolean isSyncScheduled(String syncId) {
    return PendingIntent.getService(context, 0, buildIntent(syncId, false),
        PendingIntent.FLAG_NO_CREATE) != null;
  }

  private Intent buildIntent(String syncId, boolean reschedule) {
    final Intent intent = new Intent(context, serviceClass);
    intent.setAction(ACTION_SYNC);
    intent.setData(Uri.fromParts(URI_SCHEME, URI_SCHEME_SPECIFIC_PART, syncId));
    intent.putExtra(EXTRA_RESCHEDULE, reschedule);
    return intent;
  }

  private PendingIntent getPendingIntent(Intent intent) {
    return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }
}
