package cm.aptoide.pt.notification.sync;

import android.content.SharedPreferences;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.notification.NotificationService;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.sync.Sync;

public class NotificationSyncFactory {

  public static final String SOCIAL_NOTIFICATION_SYNC_PERIODIC = "SOCIAL";
  public static final String CAMPAIGN_NOTIFICATION_SYNC_PERIODIC = "CAMPAIGN";
  public static final String CAMPAIGN_NOTIFICATION_SYNC_IMMEDIATE = "CAMPAIGN_IMMEDIATE";
  public static final String SOCIAL_NOTIFICATION_SYNC_IMMEDIATE = "SOCIAL_IMMEDIATE";

  private final SharedPreferences sharedPreferences;
  private final NotificationService notificationService;
  private final NotificationProvider notificationPersistence;

  public NotificationSyncFactory(SharedPreferences sharedPreferences,
      NotificationService notificationService, NotificationProvider notificationPersistence) {
    this.sharedPreferences = sharedPreferences;
    this.notificationService = notificationService;
    this.notificationPersistence = notificationPersistence;
  }

  public Sync create(String type) {

    switch (type) {
      case SOCIAL_NOTIFICATION_SYNC_PERIODIC:
        return new SocialNotificationSync(type, notificationService, notificationPersistence, true,
            false, getSocialNotificationInterval(), 0);
      case CAMPAIGN_NOTIFICATION_SYNC_PERIODIC:
        return new CampaignNotificationSync(type, notificationService, notificationPersistence,
            true, false, BuildConfig.CAMPAIGN_NOTIFICATION_SYNC_INTERVAL_MILLIS, 0);
      case CAMPAIGN_NOTIFICATION_SYNC_IMMEDIATE:
        return new SocialNotificationSync(type, notificationService, notificationPersistence, false,
            false, 0, 0);
      case SOCIAL_NOTIFICATION_SYNC_IMMEDIATE:
        return new CampaignNotificationSync(type, notificationService, notificationPersistence,
            false, false, 0, 0);
      default:
        throw new IllegalArgumentException("Invalid sync " + type);
    }
  }

  private long getSocialNotificationInterval() {
    long socialNotificationInterval = BuildConfig.DEFAULT_SOCIAL_NOTIFICATION_SYNC_INTERVAL_MILLIS;
    if (ToolboxManager.getPushNotificationPullingInterval(sharedPreferences) > 0) {
      socialNotificationInterval =
          ToolboxManager.getPushNotificationPullingInterval(sharedPreferences);
    }
    return socialNotificationInterval;
  }
}
