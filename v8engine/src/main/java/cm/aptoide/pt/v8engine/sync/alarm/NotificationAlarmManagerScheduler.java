package cm.aptoide.pt.v8engine.sync.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import cm.aptoide.pt.v8engine.notification.NotificationSyncScheduler;
import java.util.List;

public class NotificationAlarmManagerScheduler implements NotificationSyncScheduler {
  private final Context context;
  private final AlarmManager alarmManager;
  private final Class<? extends Service> serviceClass;
  private final List<Schedule> scheduleList;
  private boolean enabled;

  public NotificationAlarmManagerScheduler(Context context, AlarmManager alarmManager,
      Class<? extends Service> serviceClass, List<Schedule> scheduleList, boolean enabled) {
    this.context = context;
    this.alarmManager = alarmManager;
    this.serviceClass = serviceClass;
    this.scheduleList = scheduleList;
    this.enabled = enabled;
  }

  @Override public void schedule() {
    if (enabled) {
      for (final Schedule schedule : scheduleList) {
        if (!isAlarmActive(schedule)) {
          alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, schedule.getInterval(),
              getPendingIntent(schedule));
        }
      }
    }
  }

  @Override public void forceSync() {
    if (enabled) {
      for (Schedule schedule : scheduleList) {
        context.startService(buildIntent(schedule));
      }
    }
  }

  @Override public void removeSchedules() {
    for (final Schedule schedule : scheduleList) {
      PendingIntent pendingIntent = getPendingIntent(schedule);
      alarmManager.cancel(pendingIntent);
      pendingIntent.cancel();
    }
  }

  @Override public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  private boolean isAlarmActive(Schedule schedule) {
    return PendingIntent.getService(context, 0, buildIntent(schedule), PendingIntent.FLAG_NO_CREATE)
        != null;
  }

  private PendingIntent getPendingIntent(Schedule schedule) {
    Intent intent = buildIntent(schedule);
    return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  @NonNull private Intent buildIntent(Schedule schedule) {
    Intent intent = new Intent(context, serviceClass);
    intent.setAction(schedule.getAction());
    return intent;
  }

  public static class Schedule {
    private final String action;
    private final long interval;

    public Schedule(String action, long interval) {

      this.action = action;
      this.interval = interval;
    }

    public String getAction() {
      return action;
    }

    long getInterval() {
      return interval;
    }
  }
}
