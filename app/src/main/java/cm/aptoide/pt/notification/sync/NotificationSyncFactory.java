package cm.aptoide.pt.notification.sync;

import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.notification.NotificationService;
import cm.aptoide.pt.sync.Sync;

public class NotificationSyncFactory {

  public static final String CAMPAIGN_NOTIFICATION_SYNC_PERIODIC = "CAMPAIGN";
  public static final String CAMPAIGN_NOTIFICATION_SYNC_IMMEDIATE = "CAMPAIGN_IMMEDIATE";

  private final NotificationService notificationService;
  private final NotificationProvider notificationPersistence;

  public NotificationSyncFactory(NotificationService notificationService,
      NotificationProvider notificationPersistence) {
    this.notificationService = notificationService;
    this.notificationPersistence = notificationPersistence;
  }

  public Sync create(String type) {

    switch (type) {
      case CAMPAIGN_NOTIFICATION_SYNC_PERIODIC:
        return new CampaignNotificationSync(type, notificationService, notificationPersistence,
            true, false, BuildConfig.CAMPAIGN_NOTIFICATION_SYNC_INTERVAL_MILLIS, 0);
      case CAMPAIGN_NOTIFICATION_SYNC_IMMEDIATE:
        return new CampaignNotificationSync(type, notificationService, notificationPersistence,
            false, false, 0, 0);
      default:
        throw new IllegalArgumentException("Invalid sync " + type);
    }
  }
}
