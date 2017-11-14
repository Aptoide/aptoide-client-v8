package cm.aptoide.pt.notification;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.analytics.events.KnockEvent;
import com.facebook.appevents.AppEventsLogger;
import okhttp3.OkHttpClient;

/**
 * Created by trinkes on 18/09/2017.
 */

public class NotificationAnalytics {

  private static final String NOTIFICATION_IMPRESSION = "Aptoide_Push_Notification_Impression";

  private static final String TYPE = "type";
  private static final String AB_TESTING_GROUP = "ab_testing_group";
  private static final String PACKAGE_NAME = "package_name";
  private static final String CAMPAIGN_ID = "campaign_id";
  private final Analytics analytics;
  private OkHttpClient client;
  private AppEventsLogger facebook;

  public NotificationAnalytics(OkHttpClient client, Analytics analytics) {
    this.client = client;
    this.analytics = analytics;
  }

  public NotificationAnalytics(Analytics analytics, AppEventsLogger facebook) {
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

  private Bundle createSocialImpressionEventBundle(@AptoideNotification.NotificationType int type,
      String abTestingGroup, int campaignId, String url) {
    Bundle bundle = new Bundle();
    bundle.putInt(TYPE, type);
    bundle.putString(AB_TESTING_GROUP, abTestingGroup);
    bundle.putInt(CAMPAIGN_ID, campaignId);
    bundle.putString(PACKAGE_NAME, getPackageNameFromUrl(url));
    return bundle;
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
}
