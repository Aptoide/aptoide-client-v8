package cm.aptoide.pt.v8engine.usagestatsmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cm.aptoide.pt.v8engine.usagestatsmanager.utils.AlarmHelper;

public class BootOpenedAppsReceiver extends BroadcastReceiver {

  public static final long ALARM_INTERVAL = 60000;

  @Override public void onReceive(Context context, Intent intent) {
    if (intent.getAction()
        .equals(Intent.ACTION_BOOT_COMPLETED)) {
      new AlarmHelper(context, OpenedAppsReceiver.class).setupAlarm(ALARM_INTERVAL);
    }
  }
}