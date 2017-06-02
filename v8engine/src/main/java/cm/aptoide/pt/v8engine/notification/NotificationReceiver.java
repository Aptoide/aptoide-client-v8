package cm.aptoide.pt.v8engine.notification;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.realm.Notification;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagedKeys;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.preferences.Preferences;
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
  private static final String TAG = NotificationReceiver.class.getSimpleName();
  private CrashReport crashReport;
  private NotificationAccessor notificationAccessor;
  private NotificationIdsMapper notificationIdsMapper;
  private NotificationCenter notificationCenter;
  private Preferences preferences;

  @Override public void onReceive(Context context, Intent intent) {
    Logger.d(TAG,
        "onReceive() called with: " + "context = [" + context + "], intent = [" + intent + "]");
    notificationAccessor = AccessorFactory.getAccessorFor(Notification.class);
    crashReport = CrashReport.getInstance();
    notificationIdsMapper = new NotificationIdsMapper();
    notificationCenter = ((V8Engine) context.getApplicationContext()).getNotificationCenter();
    preferences = ((V8Engine) context.getApplicationContext()).getPreferences();
    String action = intent.getAction();
    if (action != null) {
      switch (action) {
        case Intent.ACTION_BOOT_COMPLETED:
          startSync().subscribe(() -> {
          }, throwable -> crashReport.log(throwable));
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

  private Completable startSync() {
    return preferences.getBoolean(ManagedKeys.CAMPAIGN_SOCIAL_NOTIFICATIONS_PREFERENCE_VIEW_KEY,
        true)
        .first()
        .filter(isEnable -> isEnable)
        .doOnNext(isEnable -> notificationCenter.start())
        .toCompletable();
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
    if (!TextUtils.isEmpty(trackUrl)) {
      DataproviderUtils.knock(trackUrl);
    }
    String targetUrl = intent.getStringExtra(NOTIFICATION_TARGET_URL);
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