package cm.aptoide.pt.v8engine.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.services.PullingContentService;

/**
 * Created by trinkes on 7/13/16.
 */
public class PullingContentReceiver extends BroadcastReceiver {

  public static final String NOTIFICATION_PRESSED_ACTION = "NOTIFICATION_PRESSED_ACTION";
  public static final String PUSH_NOTIFICATION_TRACK_URL = "PUSH_NOTIFICATION_TRACK_URL";
  public static final String PUSH_NOTIFICATION_TARGET_URL = "PUSH_NOTIFICATION_TARGET_URL";
  public static final String BOOT_COMPLETED_ACTION = "BOOT_COMPLETED_ACTION";
  private static final String TAG = PullingContentReceiver.class.getSimpleName();

  @Override public void onReceive(Context context, Intent intent) {
    Logger.d(TAG,
        "onReceive() called with: " + "context = [" + context + "], intent = [" + intent + "]");
    String action = intent.getAction();
    if (action != null) {
      switch (action) {
        case Intent.ACTION_BOOT_COMPLETED:
          context.startService(
              new Intent(context, PullingContentService.class).setAction(BOOT_COMPLETED_ACTION));
          break;
        case NOTIFICATION_PRESSED_ACTION:
          pushNotificationPressed(context, intent);
          break;
      }
    }
  }

  private void pushNotificationPressed(Context context, Intent intent) {
    String trackUrl = intent.getStringExtra(PUSH_NOTIFICATION_TRACK_URL);
    if (!TextUtils.isEmpty(trackUrl)) {
      DataproviderUtils.knock(trackUrl);
    }
    String targetUrl = intent.getStringExtra(PUSH_NOTIFICATION_TARGET_URL);
    if (!TextUtils.isEmpty(targetUrl)) {
      Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(i);
    }
  }
}