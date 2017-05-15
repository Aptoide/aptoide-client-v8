package cm.aptoide.pt.v8engine.pull;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;

/**
 * Created by trinkes on 7/13/16.
 */
public class PullingContentReceiver extends BroadcastReceiver {

  public static final String NOTIFICATION_PRESSED_ACTION = "NOTIFICATION_PRESSED_ACTION";
  public static final String PUSH_NOTIFICATION_TRACK_URL = "PUSH_NOTIFICATION_TRACK_URL";
  public static final String PUSH_NOTIFICATION_TARGET_URL = "PUSH_NOTIFICATION_TARGET_URL";
  public static final String PUSH_NOTIFICATION_DISMISSED = "PUSH_NOTIFICATION_DISMISSED";
  public static final String PUSH_NOTIFICATION_NOTIFICATION_ID =
      "PUSH_NOTIFICATION_NOTIFICATION_ID";
  private static final String TAG = PullingContentReceiver.class.getSimpleName();
  private NotificationStatusManager notificationStatusManager;

  @Override public void onReceive(Context context, Intent intent) {
    Logger.d(TAG,
        "onReceive() called with: " + "context = [" + context + "], intent = [" + intent + "]");

    notificationStatusManager =
        new NotificationStatusManager(SecurePreferencesImplementation.getInstance(context));
    String action = intent.getAction();
    if (action != null) {
      switch (action) {
        case Intent.ACTION_BOOT_COMPLETED:
          bootCompleted();
          break;
        case NOTIFICATION_PRESSED_ACTION:
          pushNotificationPressed(context, intent);
          notificationDismissed(intent.getIntExtra(PUSH_NOTIFICATION_NOTIFICATION_ID, -1));
          break;
        case PUSH_NOTIFICATION_DISMISSED:
          if (intent.hasExtra(PUSH_NOTIFICATION_NOTIFICATION_ID)) {
            notificationDismissed(intent.getIntExtra(PUSH_NOTIFICATION_NOTIFICATION_ID, -1));
          }
          break;
      }
    }
  }

  private void bootCompleted() {
    notificationStatusManager.reset();
  }

  private void notificationDismissed(int notificationId) {
    notificationStatusManager.setVisible(notificationId, false);
  }

  private void pushNotificationPressed(Context context, Intent intent) {
    String trackUrl = intent.getStringExtra(PUSH_NOTIFICATION_TRACK_URL);
    if (!TextUtils.isEmpty(trackUrl)) {
      DataproviderUtils.knock(trackUrl);
    }
    String targetUrl = intent.getStringExtra(PUSH_NOTIFICATION_TARGET_URL);
    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    try {
      context.startActivity(i);
    } catch (ActivityNotFoundException e) {
      CrashReport.getInstance()
          .log(TAG, "No application can handle this request. Please install a webbrowser");
    }
  }
}