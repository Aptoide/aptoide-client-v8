package cm.aptoide.pt.notification;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.analytics.events.KnockEvent;
import cm.aptoide.pt.database.realm.Notification;
import com.facebook.appevents.AppEventsLogger;
import okhttp3.OkHttpClient;

/**
 * Created by trinkes on 18/09/2017.
 */

public class NotificationAnalytics {

  private static final String NOTIFICATION_IMPRESSION = "Aptoide_Push_Notification_Impression";
  private static final String NOTIFICATION_PRESSED = "Aptoide_Push_Notification_Click";

  private static final String TYPE = "type";
  private static final String AB_TESTING_GROUP = "ab_testing_group";
  private static final String PACKAGE_NAME = "package_name";
  private static final String CAMPAIGN_ID = "campaign_id";
  private final Analytics analytics;
  private OkHttpClient client;
  private AppEventsLogger facebook;

  public NotificationAnalytics(OkHttpClient client, Analytics analytics, AppEventsLogger facebook) {
    this.client = client;
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void notificationShown(String url) {
    analytics.sendEvent(new KnockEvent(url, client));
  }

  public void sendUpdatesNotificationReceivedEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, NOTIFICATION_IMPRESSION));
  }

  public void sendSocialNotificationReceivedEvent(@AptoideNotification.NotificationType int type,
      String abTestingGroup, int campaignId, String url) {
    analytics.sendEvent(new FacebookEvent(facebook, NOTIFICATION_IMPRESSION,
        createSocialImpressionEventBundle(type, abTestingGroup, campaignId, url)));
  }

  public void sendSocialNotificationPressedEvent(Notification notification) {
    analytics.sendEvent(new FacebookEvent(facebook, NOTIFICATION_PRESSED,
        createSocialNotificationPressedEventBundle(notification)));
  }

  public void sendUpdatesNotificationClickEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, NOTIFICATION_PRESSED, createUpdateClickEventBundle()));
  }

  private Bundle createUpdateClickEventBundle() {
    Bundle bundle = new Bundle();
    bundle.putString(TYPE, NotificationTypes.CAMPAIGN.toString()
        .toLowerCase());
    return bundle;
  }

  private Bundle createSocialNotificationPressedEventBundle(Notification notification) {
    Bundle bundle = new Bundle();
    bundle.putInt(TYPE, notification.getType());
    bundle.putString(AB_TESTING_GROUP, notification.getAbTestingGroup());
    bundle.putInt(CAMPAIGN_ID, notification.getCampaignId());
    bundle.putString(PACKAGE_NAME, getPackageNameFromUrl(notification.getUrl()));
    return bundle;
  }

  private Bundle createSocialImpressionEventBundle(@AptoideNotification.NotificationType int type,
      String abTestingGroup, int campaignId, String url) {
    Bundle bundle = new Bundle();
    bundle.putString(TYPE, matchNotificationTypeToString(type).toString()
        .toLowerCase());
    bundle.putString(AB_TESTING_GROUP, abTestingGroup);
    bundle.putInt(CAMPAIGN_ID, campaignId);
    bundle.putString(PACKAGE_NAME, getPackageNameFromUrl(url));
    return bundle;
  }

  private NotificationTypes matchNotificationTypeToString(int type) {
    switch (type) {
      case 0:
        return NotificationTypes.CAMPAIGN;
      case 1:
        return NotificationTypes.LIKE;
      case 2:
        return NotificationTypes.COMMENT;
      case 3:
        return NotificationTypes.POPULAR;
      case 4:
        return NotificationTypes.NEW_FOLLOWER;
      case 5:
        return NotificationTypes.NEW_SHARE;
      case 6:
        return NotificationTypes.NEW_ACTIVITY;
    }
    return NotificationTypes.OTHER;
  }

  private String getPackageNameFromUrl(String url) {
    String[] split = url.split("&");
    for (String part : split) {
      if (part.contains("package")) {
        return part.split("=")[1];
      }
    }
    return "";
  }

  private enum NotificationTypes {
    CAMPAIGN, LIKE, COMMENT, POPULAR, NEW_FOLLOWER, NEW_SHARE, NEW_ACTIVITY, OTHER, UPDATES
  }
}
