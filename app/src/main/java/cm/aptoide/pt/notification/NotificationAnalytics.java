package cm.aptoide.pt.notification;

import android.support.annotation.NonNull;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.link.AptoideInstall;
import cm.aptoide.pt.link.AptoideInstallParser;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 18/09/2017.
 */

public class NotificationAnalytics {

  public static final String NOTIFICATION_TOUCH = "NOTIFICATION_TOUCH";
  public static final String NOTIFICATION_RECEIVED = "Aptoide_Push_Notification_Received";
  public static final String NOTIFICATION_PRESSED = "Aptoide_Push_Notification_Click";
  public static final String NOTIFICATION_EVENT_NAME = "NOTIFICATION";
  private static final String NOTIFICATION_IMPRESSION = "Aptoide_Push_Notification_Impression";
  private static final String TYPE = "type";
  private static final String AB_TESTING_GROUP = "ab_testing_group";
  private static final String PACKAGE_NAME = "package_name";
  private static final String CAMPAIGN_ID = "campaign_id";
  private static final String DEFAULT_CONTEXT = "Notification";
  private final AptoideInstallParser aptoideInstallParser;
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;
  private AptoideInstall aptoideInstall;
  private int campaignId;
  private String abTestingGroup;

  public NotificationAnalytics(AptoideInstallParser aptoideInstallParser,
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    this.aptoideInstallParser = aptoideInstallParser;
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void sendNotificationTouchEvent(String url) {
    analyticsManager.logEvent(url);
  }

  public void sendUpdatesNotificationReceivedEvent() {
    analyticsManager.logEvent(createUpdateNotificationEventsMap(), NOTIFICATION_RECEIVED,
        AnalyticsManager.Action.AUTO, getViewName(true));
  }

  public void sendUpdatesNotificationClickEvent() {
    analyticsManager.logEvent(createUpdateNotificationEventsMap(), NOTIFICATION_PRESSED,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendPushNotificationReceivedEvent(@AptoideNotification.NotificationType int type,
      String abTestingGroup, int campaignId, String url) {
    analyticsManager.logEvent(createPushNotificationEventMap(type, abTestingGroup, campaignId, url),
        NOTIFICATION_RECEIVED, AnalyticsManager.Action.VIEW, getViewName(true));
  }

  public void sendPushNotficationImpressionEvent(@AptoideNotification.NotificationType int type,
      String abTestingGroup, int campaignId, String url) {
    if (type == AptoideNotification.CAMPAIGN) {
      analyticsManager.logEvent(createCampaignNotificationMap(abTestingGroup, campaignId),
          NOTIFICATION_EVENT_NAME, AnalyticsManager.Action.IMPRESSION, getViewName(true));
    }
    analyticsManager.logEvent(createPushNotificationEventMap(type, abTestingGroup, campaignId, url),
        NOTIFICATION_IMPRESSION, AnalyticsManager.Action.IMPRESSION, getViewName(true));
  }

  @NonNull
  private Map<String, Object> createCampaignNotificationMap(String abTestingGroup, int campaignId) {
    Map<String, Object> map = new HashMap<>();
    map.put(CAMPAIGN_ID, campaignId);
    map.put(AB_TESTING_GROUP, abTestingGroup);
    return map;
  }

  public void sendPushNotificationPressedEvent(@AptoideNotification.NotificationType int type,
      String abTestingGroup, int campaignId, String url) {
    if (type == AptoideNotification.CAMPAIGN) {
      analyticsManager.logEvent(createCampaignNotificationMap(abTestingGroup, campaignId),
          NOTIFICATION_EVENT_NAME, AnalyticsManager.Action.OPEN, getViewName(true));
    }
    analyticsManager.logEvent(createPushNotificationEventMap(type, abTestingGroup, campaignId, url),
        NOTIFICATION_PRESSED, AnalyticsManager.Action.OPEN, getViewName(true));
  }

  private Map<String, Object> createUpdateNotificationEventsMap() {
    Map<String, Object> map = new HashMap<>();
    map.put(TYPE, NotificationTypes.UPDATES.toString()
        .toLowerCase());
    return map;
  }

  private Map<String, Object> createPushNotificationEventMap(
      @AptoideNotification.NotificationType int type, String abTestingGroup, int campaignId,
      String url) {
    Map<String, Object> map = new HashMap<>();
    map.put(CAMPAIGN_ID, String.valueOf(campaignId));
    map.put(TYPE, matchNotificationTypeToString(type).toString()
        .toLowerCase());
    map = addToMapIfNotNull(map, abTestingGroup, getPackageNameFromUrl(url));
    return map;
  }

  private Map<String, Object> addToMapIfNotNull(Map<String, Object> map, String abTestingGroup,
      String url) {
    if (abTestingGroup != null && !abTestingGroup.isEmpty()) {
      map.put(AB_TESTING_GROUP, abTestingGroup);
    }
    if (url != null && !url.isEmpty()) {
      map.put(PACKAGE_NAME, getPackageNameFromUrl(url));
    }
    return map;
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

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent, DEFAULT_CONTEXT);
  }

  private enum NotificationTypes {
    CAMPAIGN, LIKE, COMMENT, POPULAR, NEW_FOLLOWER, NEW_SHARE, NEW_ACTIVITY, OTHER, UPDATES
  }
}
