package cm.aptoide.pt.notification;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.AptoideEvent;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.analytics.events.KnockEvent;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.link.AptoideInstall;
import cm.aptoide.pt.link.AptoideInstallParser;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 18/09/2017.
 */

public class NotificationAnalytics {

  public static final String IMPRESSION_ACTION = "IMPRESSION";
  public static final String NOTIFICATION_BAR_CONTEXT = "NOTIFICATION_BAR";
  private static final String OPEN_ACTION = "OPEN";
  private static final String NOTIFICATION_RECEIVED = "Aptoide_Push_Notification_Received";
  private static final String NOTIFICATION_IMPRESSION = "Aptoide_Push_Notification_Impression";
  private static final String NOTIFICATION_PRESSED = "Aptoide_Push_Notification_Click";
  private static final String NOTIFICATION_EVENT_NAME = "NOTIFICATION";
  private static final String TYPE = "type";
  private static final String AB_TESTING_GROUP = "ab_testing_group";
  private static final String PACKAGE_NAME = "package_name";
  private static final String CAMPAIGN_ID = "campaign_id";
  private final Analytics analytics;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final String appId;
  private final SharedPreferences sharedPreferences;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final AptoideInstallParser aptoideInstallParser;
  private final AppEventsLogger facebook;
  private AptoideInstall aptoideInstall;
  private int campaignId;
  private String abTestingGroup;

  public NotificationAnalytics(Analytics analytics, AppEventsLogger facebook,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator, String appId,
      SharedPreferences sharedPreferences, AptoideInstallParser aptoideInstallParser) {
    this.analytics = analytics;
    this.facebook = facebook;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.appId = appId;
    this.sharedPreferences = sharedPreferences;
    this.aptoideInstallParser = aptoideInstallParser;
  }

  public void sendNotificationTouchEvent(String url) {
    analytics.sendEvent(new KnockEvent(url, httpClient));
  }

  public void sendUpdatesNotificationReceivedEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, NOTIFICATION_RECEIVED, createUpdateNotificationEventsBundle()));
  }

  public void sendUpdatesNotificationClickEvent() {
    analytics.sendEvent(
        new FacebookEvent(facebook, NOTIFICATION_PRESSED, createUpdateNotificationEventsBundle()));
  }

  public void sendPushNotificationReceivedEvent(@AptoideNotification.NotificationType int type,
      String abTestingGroup, int campaignId, String url) {
    analytics.sendEvent(new FacebookEvent(facebook, NOTIFICATION_RECEIVED,
        createPushNotificationEventBundle(type, abTestingGroup, campaignId, url)));
  }

  public void sendPushNotficationImpressionEvent(@AptoideNotification.NotificationType int type,
      String abTestingGroup, int campaignId, String url) {
    if (type == AptoideNotification.CAMPAIGN) {
      analytics.sendEvent(
          new AptoideEvent(createCampaignNotificationMap(abTestingGroup, campaignId),
              NOTIFICATION_EVENT_NAME, IMPRESSION_ACTION, NOTIFICATION_BAR_CONTEXT, bodyInterceptor,
              httpClient, converterFactory, tokenInvalidator, appId, sharedPreferences));
    }
    analytics.sendEvent(new FacebookEvent(facebook, NOTIFICATION_IMPRESSION,
        createPushNotificationEventBundle(type, abTestingGroup, campaignId, url)));
  }

  @NonNull
  private Map<String, Object> createCampaignNotificationMap(String abTestingGroup, int campaignId) {
    Map<String, Object> map = new HashMap<>();
    map.put("campaign_id", campaignId);
    map.put("ab_testing_group", abTestingGroup);
    return map;
  }

  public void sendPushNotificationPressedEvent(@AptoideNotification.NotificationType int type,
      String abTestingGroup, int campaignId, String url) {
    if (type == AptoideNotification.CAMPAIGN) {
      analytics.sendEvent(
          new AptoideEvent(createCampaignNotificationMap(abTestingGroup, campaignId),
              NOTIFICATION_EVENT_NAME, OPEN_ACTION, NOTIFICATION_BAR_CONTEXT, bodyInterceptor,
              httpClient, converterFactory, tokenInvalidator, appId, sharedPreferences));
    }
    analytics.sendEvent(new FacebookEvent(facebook, NOTIFICATION_PRESSED,
        createPushNotificationEventBundle(type, abTestingGroup, campaignId, url)));
  }

  private Bundle createUpdateNotificationEventsBundle() {
    Bundle bundle = new Bundle();
    bundle.putString(TYPE, NotificationTypes.UPDATES.toString()
        .toLowerCase());
    return bundle;
  }

  private Bundle createPushNotificationEventBundle(@AptoideNotification.NotificationType int type,
      String abTestingGroup, int campaignId, String url) {
    Bundle bundle = new Bundle();
    bundle.putInt(CAMPAIGN_ID, campaignId);
    bundle.putString(TYPE, matchNotificationTypeToString(type).toString()
        .toLowerCase());
    bundle = addToBundleIfNotNull(bundle, abTestingGroup, getPackageNameFromUrl(url));
    return bundle;
  }

  private Bundle addToBundleIfNotNull(Bundle bundle, String abTestingGroup, String url) {
    if (abTestingGroup != null && !abTestingGroup.isEmpty()) {
      bundle.putString(AB_TESTING_GROUP, abTestingGroup);
    }
    if (url != null && !url.isEmpty()) {
      bundle.putString(PACKAGE_NAME, getPackageNameFromUrl(url));
    }
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

  public void sendNotificationTouchEvent(String trackUrl,
      @AptoideNotification.NotificationType int notificationType, String url, int campaignId,
      String abTestingGroup) {
    sendNotificationTouchEvent(trackUrl);
    if (notificationType == AptoideNotification.CAMPAIGN) {
      AptoideInstall aptoideInstall = aptoideInstallParser.parse(url);
      if (aptoideInstall.getAppId() > 0
          || aptoideInstall.getPackageName() != null && !aptoideInstall.getPackageName()
          .isEmpty()) {
        this.aptoideInstall = aptoideInstall;
        this.campaignId = campaignId;
        this.abTestingGroup = abTestingGroup;
      }
    }
  }

  public int getCampaignId(String packageName, long appId) {
    if (isSameApp(packageName, appId)) {
      int aux = this.campaignId;
      campaignId = 0;
      return aux;
    } else {
      return 0;
    }
  }

  private boolean isSameApp(String packageName, long appId) {
    return aptoideInstall != null && (aptoideInstall.getPackageName()
        .equals(packageName) || aptoideInstall.getAppId() == appId);
  }

  public String getAbTestingGroup(String packageName, long appId) {
    if (isSameApp(packageName, appId)) {
      String aux = this.abTestingGroup;
      abTestingGroup = null;
      return aux;
    } else {
      return "";
    }
  }

  private enum NotificationTypes {
    CAMPAIGN, LIKE, COMMENT, POPULAR, NEW_FOLLOWER, NEW_SHARE, NEW_ACTIVITY, OTHER, UPDATES
  }
}
