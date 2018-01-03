package cm.aptoide.pt.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cm.aptoide.pt.AptoideApplication;
import com.jakewharton.rxrelay.PublishRelay;

public class NotificationReceiver extends BroadcastReceiver {

  public static final String NOTIFICATION_PRESSED_ACTION = "NOTIFICATION_PRESSED_ACTION";
  public static final String NOTIFICATION_TRACK_URL = "PUSH_NOTIFICATION_TRACK_URL";
  public static final String NOTIFICATION_TARGET_URL = "PUSH_NOTIFICATION_TARGET_URL";
  public static final String NOTIFICATION_DISMISSED_ACTION = "PUSH_NOTIFICATION_DISMISSED";
  public static final String NOTIFICATION_NOTIFICATION_ID = "PUSH_NOTIFICATION_NOTIFICATION_ID";
  public static final String ACTION = "action";

  private PublishRelay<NotificationInfo> notificationPublishRelay;

  @Override public void onReceive(Context context, Intent intent) {
    notificationPublishRelay =
        ((AptoideApplication) context.getApplicationContext()).getNotificationsPublishRelay();
    Bundle intentExtras = intent.getExtras();
    NotificationInfo notificationInfo;
    switch (intent.getAction()) {
      case Intent.ACTION_BOOT_COMPLETED:
        notificationInfo = new NotificationInfo(Intent.ACTION_BOOT_COMPLETED);
        notificationPublishRelay.call(notificationInfo);
        break;
      case NOTIFICATION_PRESSED_ACTION:
        notificationInfo = new NotificationInfo(NOTIFICATION_PRESSED_ACTION,
            intentExtras.getInt(NOTIFICATION_NOTIFICATION_ID),
            intentExtras.getString(NOTIFICATION_TRACK_URL),
            intentExtras.getString(NOTIFICATION_TARGET_URL));
        notificationPublishRelay.call(notificationInfo);
        break;
      case NOTIFICATION_DISMISSED_ACTION:
        notificationInfo = new NotificationInfo(NOTIFICATION_DISMISSED_ACTION,
            intentExtras.getInt(NOTIFICATION_NOTIFICATION_ID),
            intentExtras.getString(NOTIFICATION_TRACK_URL),
            intentExtras.getString(NOTIFICATION_TARGET_URL));
        notificationPublishRelay.call(notificationInfo);
        break;
    }
  }
}
