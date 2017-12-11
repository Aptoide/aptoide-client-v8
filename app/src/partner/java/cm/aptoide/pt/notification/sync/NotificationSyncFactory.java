package cm.aptoide.pt.notification.sync;

import android.content.SharedPreferences;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.notification.NotificationService;
import cm.aptoide.pt.sync.Sync;

public class NotificationSyncFactory {

  public static final String PUSH_NOTIFICATION_SYNC_PERIODIC = "PUSH_NOTIFICATION_PERIODIC";
  public static final String PUSH_NOTIFICATION_SYNC_IMMEDIATE = "PUSH_NOTIFICATION_IMMEDIATE";

  private final NotificationService notificationService;
  private final NotificationProvider notificationPersistence;
  private final SharedPreferences sharedPreferences;

  public NotificationSyncFactory(NotificationService notificationService,
      NotificationProvider notificationPersistence, SharedPreferences sharedPreferences) {
    this.notificationService = notificationService;
    this.notificationPersistence = notificationPersistence;
    this.sharedPreferences = sharedPreferences;
  }

  public Sync create(String type) {
    switch (type) {
      case PUSH_NOTIFICATION_SYNC_IMMEDIATE:
        return new PushNotificationSync(type, notificationService, notificationPersistence, false,
            false, BuildConfig.CAMPAIGN_NOTIFICATION_SYNC_INTERVAL_MILLIS, 0, sharedPreferences);
      case PUSH_NOTIFICATION_SYNC_PERIODIC:
        return new PushNotificationSync(type, notificationService, notificationPersistence, true,
            false, 0, 0, sharedPreferences);
      default:
        throw new IllegalArgumentException("Invalid sync " + type);
    }
  }
}
