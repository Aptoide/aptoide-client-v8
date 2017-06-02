package cm.aptoide.pt.v8engine.usagestatsmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public abstract class WakelockReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {
    PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
    wakeLock.acquire();

    wakeLockReceive(context, intent);

    wakeLock.release();
  }

  protected abstract void wakeLockReceive(Context context, Intent intent);
}