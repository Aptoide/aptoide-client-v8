package cm.aptoide.pt.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FabricEvent;
import com.crashlytics.android.answers.Answers;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.HashMap;
import java.util.Map;

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
    final String action = intent.getAction();
    Bundle intentExtras = intent.getExtras();
    if (intentExtras != null) {
      Map<String, String> stringStringMap = new HashMap<>();
      stringStringMap.put("status", "success");
      Analytics.getInstance()
          .sendEvent(new FabricEvent(Answers.getInstance(), "NotificationNPE", stringStringMap));

      NotificationInfo notificationInfo =
          new NotificationInfo(intentExtras.getInt(NOTIFICATION_NOTIFICATION_ID),
              intentExtras.getString(NOTIFICATION_TRACK_URL),
              intentExtras.getString(NOTIFICATION_TARGET_URL));
      if (action != null) {
        switch (action) {
          case Intent.ACTION_BOOT_COMPLETED:
            notificationInfo.setAction(Intent.ACTION_BOOT_COMPLETED);
            notificationPublishRelay.call(notificationInfo);
            break;
          case NOTIFICATION_PRESSED_ACTION:
            notificationInfo.setAction(NOTIFICATION_PRESSED_ACTION);
            notificationPublishRelay.call(notificationInfo);
            break;
          case NOTIFICATION_DISMISSED_ACTION:
            notificationInfo.setAction(NOTIFICATION_DISMISSED_ACTION);
            notificationPublishRelay.call(notificationInfo);
            break;
        }
      }
    } else {
      Map<String, String> stringStringMap = new HashMap<>();
      stringStringMap.put("status", "fail");
      stringStringMap.put("Intent", intent.toString());
      Analytics.getInstance()
          .sendEvent(new FabricEvent(Answers.getInstance(), "NotificationNPE", stringStringMap));
    }
  }
}
