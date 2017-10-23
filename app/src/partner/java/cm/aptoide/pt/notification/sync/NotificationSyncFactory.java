package cm.aptoide.pt.notification.sync;

import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.notification.NotificationService;
import cm.aptoide.pt.notification.PushNotificationSync;
import cm.aptoide.pt.sync.Sync;

public class NotificationSyncFactory {

  public static final String PUSH_NOTIFICATION_SYNC_PERIODIC = "PERIODIC";
  public static final String PUSH_NOTIFICATION_SYNC_IMMEDIATE = "IMMEDIATE";

  private final NotificationService notificationService;
  private final NotificationProvider notificationProvider;

  public NotificationSyncFactory(NotificationService notificationService,
      NotificationProvider notificationProvider) {
    this.notificationService = notificationService;
    this.notificationProvider = notificationProvider;
  }

  public Sync create(String type) {
    switch (type) {
      case PUSH_NOTIFICATION_SYNC_PERIODIC:
        return new PushNotificationSync(type, notificationService, notificationProvider, true,
            false, BuildConfig.CAMPAIGN_NOTIFICATION_SYNC_INTERVAL_MILLIS, 0);
      case PUSH_NOTIFICATION_SYNC_IMMEDIATE:
        return new PushNotificationSync(type, notificationService, notificationProvider, false,
            false, 0, 0);
      default:
        throw new IllegalArgumentException("Invalid sync " + type);
    }
  }
}
