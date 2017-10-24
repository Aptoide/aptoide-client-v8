package cm.aptoide.pt.notification.sync;

import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.notification.NotificationService;
import cm.aptoide.pt.notification.PushNotificationSync;
import cm.aptoide.pt.sync.Sync;

public class NotificationSyncFactory {

  public static final String PUSH_NOTIFICATION_SYNC_PERIODIC = "PUSH_NOTIFICATION_PERIODIC";
  public static final String PUSH_NOTIFICATION_SYNC_IMMEDIATE = "PUSH_NOTIFICATION_IMMEDIATE";

  private final NotificationService notificationService;
  private final NotificationProvider notificationPersistence;

  public NotificationSyncFactory(NotificationService notificationService,
      NotificationProvider notificationPersistence) {
    this.notificationService = notificationService;
    this.notificationPersistence = notificationPersistence;
  }

  public Sync create(String type) {
    switch (type) {
      case PUSH_NOTIFICATION_SYNC_IMMEDIATE:
        return new PushNotificationSync(type, notificationService, notificationPersistence, false,
            false, 0, 0);
      case PUSH_NOTIFICATION_SYNC_PERIODIC:
        return new PushNotificationSync(type, notificationService, notificationPersistence, true,
            false, 0, 0);
      default:
        throw new IllegalArgumentException("Invalid sync " + type);
    }
  }
}
