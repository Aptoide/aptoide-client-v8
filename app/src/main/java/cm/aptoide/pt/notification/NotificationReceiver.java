package cm.aptoide.pt.notification;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import rx.Completable;

/**
 * Created by trinkes on 7/13/16.
 */
public class NotificationReceiver extends BroadcastReceiver {

  public static final String NOTIFICATION_PRESSED_ACTION = "NOTIFICATION_PRESSED_ACTION";
  public static final String NOTIFICATION_TRACK_URL = "PUSH_NOTIFICATION_TRACK_URL";
  public static final String NOTIFICATION_TARGET_URL = "PUSH_NOTIFICATION_TARGET_URL";
  public static final String NOTIFICATION_DISMISSED_ACTION = "PUSH_NOTIFICATION_DISMISSED";
  public static final String NOTIFICATION_NOTIFICATION_ID = "PUSH_NOTIFICATION_NOTIFICATION_ID";

  private CrashReport crashReport;
  private NotificationIdsMapper notificationIdsMapper;
  private NotificationCenter notificationCenter;
  private NotificationAnalytics analytics;

  @Override public void onReceive(Context context, Intent intent) {
    crashReport = CrashReport.getInstance();
    analytics = new NotificationAnalytics(
        ((AptoideApplication) context.getApplicationContext()).getDefaultClient(),
        Analytics.getInstance());
    notificationIdsMapper = new NotificationIdsMapper();
    notificationCenter =
        ((AptoideApplication) context.getApplicationContext()).getNotificationCenter();
    final String action = intent.getAction();
    if (action != null) {
      switch (action) {
        case Intent.ACTION_BOOT_COMPLETED:
          notificationCenter.setup();
          break;
        case NOTIFICATION_PRESSED_ACTION:
          callDeepLink(context, intent);
          dismissNotification(intent.getIntExtra(NOTIFICATION_NOTIFICATION_ID, -1)).subscribe(
              () -> {
              }, throwable -> crashReport.log(throwable));
          break;
        case NOTIFICATION_DISMISSED_ACTION:
          if (intent.hasExtra(NOTIFICATION_NOTIFICATION_ID)) {
            dismissNotification(intent.getIntExtra(NOTIFICATION_NOTIFICATION_ID, -1)).subscribe(
                () -> {
                }, throwable -> crashReport.log(throwable));
          }
          break;
      }
    }
  }

  private Completable dismissNotification(int notificationId) {
    return Completable.defer(() -> {
      try {
        return notificationCenter.notificationDismissed(
            notificationIdsMapper.getNotificationType(notificationId));
      } catch (RuntimeException e) {
        return Completable.error(e);
      }
    });
  }

  private void callDeepLink(Context context, Intent intent) {
    String trackUrl = intent.getStringExtra(NOTIFICATION_TRACK_URL);
    analytics.sendNotificationTouchEvent(trackUrl);
    String targetUrl = intent.getStringExtra(NOTIFICATION_TARGET_URL);
    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    try {
      context.startActivity(i);
    } catch (ActivityNotFoundException e) {
      crashReport.log(e);
    }
  }
}
