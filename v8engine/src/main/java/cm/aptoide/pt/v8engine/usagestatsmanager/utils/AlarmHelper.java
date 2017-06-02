package cm.aptoide.pt.v8engine.usagestatsmanager.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by neuro on 31-05-2017.
 */
public class AlarmHelper {

  private static final String TAG = AlarmHelper.class.getSimpleName();

  private final Context context;
  private final Class<?> aClass;

  public AlarmHelper(Context context, Class<?> aClass) {
    this.context = context;
    this.aClass = aClass;
  }

  public void setupAlarm(long intervalMillis) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(context, aClass);
    PendingIntent pendingIntent =
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervalMillis,
        pendingIntent);
    System.out.println(
        TAG + ": setupAlarm called with interval " + intervalMillis + " for class " + aClass);
  }

  public void cancelAlarm() {
    Intent intent = new Intent(context, aClass);
    PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.cancel(sender);

    System.out.println(TAG + ": cancelAlarm called for class " + this.aClass);
  }

  public boolean isAlarmActive() {
    return PendingIntent.getBroadcast(context, 0, new Intent(context, aClass),
        PendingIntent.FLAG_NO_CREATE) != null;
  }
}
