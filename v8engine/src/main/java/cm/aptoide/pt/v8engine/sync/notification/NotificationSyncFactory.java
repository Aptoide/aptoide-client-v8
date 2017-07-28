package cm.aptoide.pt.v8engine.sync.notification;

import android.app.AlarmManager;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.v8engine.notification.NotificationNetworkService;
import cm.aptoide.pt.v8engine.notification.NotificationProvider;
import cm.aptoide.pt.v8engine.sync.Sync;

public class NotificationSyncFactory {

  public static final String SOCIAL_NOTIFICATION_SYNC_PERIODIC = "SOCIAL";
  public static final String CAMPAIGN_NOTIFICATION_SYNC_PERIODIC = "CAMPAIGN";
  public static final String CAMPAIGN_NOTIFICATION_SYNC_IMMEDIATE = "CAMPAIGN_IMMEDIATE";
  public static final String SOCIAL_NOTIFICATION_SYNC_IMMEDIATE = "SOCIAL_IMMEDIATE";

  private final SharedPreferences sharedPreferences;
  private final NotificationNetworkService notificationService;
  private final NotificationProvider notificationPersistence;

  public NotificationSyncFactory(SharedPreferences sharedPreferences,
      NotificationNetworkService notificationService,
      NotificationProvider notificationPersistence) {
    this.sharedPreferences = sharedPreferences;
    this.notificationService = notificationService;
    this.notificationPersistence = notificationPersistence;
  }

  public Sync create(String type) {

    switch (type) {
      case SOCIAL_NOTIFICATION_SYNC_PERIODIC:
        return new SocialNotificationSync(type, notificationService, notificationPersistence, true,
            false, getSocialNotificationInterval());
      case CAMPAIGN_NOTIFICATION_SYNC_PERIODIC:
        return new CampaignNotificationSync(type, notificationService, notificationPersistence,
            true, false, AlarmManager.INTERVAL_HALF_DAY);
      case CAMPAIGN_NOTIFICATION_SYNC_IMMEDIATE:
        return new SocialNotificationSync(type, notificationService, notificationPersistence, false,
            false, 0);
      case SOCIAL_NOTIFICATION_SYNC_IMMEDIATE:
        return new CampaignNotificationSync(type, notificationService, notificationPersistence,
            false, false, 0);
      default:
        throw new IllegalArgumentException("Invalid sync " + type);
    }
  }

  private long getSocialNotificationInterval() {
    long socialNotificationInterval = DateUtils.MINUTE_IN_MILLIS * 10;
    if (ToolboxManager.getPushNotificationPullingInterval(sharedPreferences) > 0) {
      socialNotificationInterval =
          ToolboxManager.getPushNotificationPullingInterval(sharedPreferences);
    }
    return socialNotificationInterval;
  }
}
