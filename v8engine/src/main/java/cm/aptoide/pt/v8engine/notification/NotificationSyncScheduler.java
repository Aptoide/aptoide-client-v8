package cm.aptoide.pt.v8engine.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import java.util.List;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationSyncScheduler {
  private final Context context;
  private final AlarmManager alarmManager;
  private final Class<? extends Service> serviceClass;
  private List<Schedule> scheduleList;

  public NotificationSyncScheduler(Context context, AlarmManager alarmManager,
      Class<? extends Service> serviceClass, List<Schedule> scheduleList) {
    this.context = context;
    this.alarmManager = alarmManager;
    this.serviceClass = serviceClass;
    this.scheduleList = scheduleList;
  }

  public void schedule() {

    for (final Schedule schedule : scheduleList) {
      Intent intent = new Intent(context, serviceClass);
      intent.setAction(schedule.getAction());
      PendingIntent pendingIntent =
          PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
      alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, schedule.getInterval(),
          pendingIntent);
    }
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

    public long getInterval() {
      return interval;
    }
  }
}
