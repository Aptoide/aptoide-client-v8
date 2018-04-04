package cm.aptoide.pt.timeline;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.social.data.ReadPostsPersistence;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jdandrade on 27/10/2016.
 */
public class TimelineAnalytics {

  public static final String SOURCE_APTOIDE = "APTOIDE";

  public static final String SOCIAL_CARD_ACTION_SHARE_CONTINUE = "Continue";
  public static final String SOCIAL_CARD_ACTION_SHARE_CANCEL = "Cancel";
  public static final String PREVIOUS_CONTEXT = "previous_context";
  public static final String STORE = "store";
  public static final String APPS_TIMELINE_EVENT = "Apps Timeline";
  public static final String OPEN_ARTICLE = "OPEN_ARTICLE";
  public static final String OPEN_ARTICLE_HEADER = "OPEN_ARTICLE_HEADER";
  public static final String OPEN_BLOG = "OPEN_BLOG";
  public static final String OPEN_VIDEO = "OPEN_VIDEO";
  public static final String OPEN_VIDEO_HEADER = "OPEN_VIDEO_HEADER";
  public static final String OPEN_CHANNEL = "OPEN_CHANNEL";
  public static final String OPEN_STORE = "OPEN_STORE";
  public static final String OPEN_STORE_PROFILE = "OPEN_STORE_PROFILE";
  public static final String OPEN_APP = "OPEN_APP";
  public static final String OPEN_APP_VIEW = "OPEN_APP_VIEW";
  public static final String OPEN_TIMELINE_EVENT = "OPEN_TIMELINE";
  public static final String UPDATE_APP = "UPDATE_APP";
  public static final String FOLLOW_FRIENDS = "Apps_Timeline_Follow_Friends";
  public static final String LIKE = "LIKE";
  public static final String COMMENT = "COMMENT";
  public static final String SHARE = "SHARE";
  public static final String SHARE_SEND = "SHARE_SEND";
  public static final String COMMENT_SEND = "COMMENT_SEND";
  public static final String FAB = "FAB";
  public static final String SCROLLING_EVENT = "SCROLLING";
  public static final String TIMELINE_OPENED = "Apps_Timeline_Open";
  public static final String SOCIAL_CARD_PREVIEW = "Apps_Timeline_Social_Card_Preview";
  public static final String CARD_ACTION = "Apps_Timeline_Card_Action";
  public static final String MESSAGE_IMPRESSION = "Message_Impression";
  public static final String MESSAGE_INTERACT = "Message_Interact";
  private static final String CARD_TYPE = "card_type";
  private static final String ACTION = "action";
  private static final String SOCIAL_ACTION = "social_action";
  private static final String PACKAGE = "package_name";
  private static final String PUBLISHER = "publisher";
  private static final String TITLE = "title";
  private static final String BLANK = "(blank)";
  private static final String TIMELINE_VERSION = "timeline_version";
  private static final String SOURCE = "source";
  private static final String APPS_SHORTCUTS = "apps_shortcuts";
  private static final String EXTERNAL = "EXTERNAL";
  private final NotificationAnalytics notificationAnalytics;
  private final NavigationTracker navigationTracker;
  private final ReadPostsPersistence readPostsPersistence;
  private final List<Map<String, Object>> openTimelineEventsData;
  private final AnalyticsManager analyticsManager;

  public TimelineAnalytics(NotificationAnalytics notificationAnalytics,
      NavigationTracker navigationTracker, ReadPostsPersistence readPostsPersistence,
      AnalyticsManager analyticsManager) {
    this.notificationAnalytics = notificationAnalytics;
    this.navigationTracker = navigationTracker;
    this.readPostsPersistence = readPostsPersistence;
    this.analyticsManager = analyticsManager;
    this.openTimelineEventsData = new ArrayList<>();
  }

  public void sendSocialCardPreviewActionEvent(String value) {
    analyticsManager.logEvent(createMapData(ACTION, value), SOCIAL_CARD_PREVIEW,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendSocialActionEvent(TimelineSocialActionData timelineSocialActionData) {
    analyticsManager.logEvent(createSocialActionEventData(timelineSocialActionData), CARD_ACTION,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createSocialActionEventData(
      TimelineSocialActionData timelineSocialActionData) {
    Map<String, Object> data = new HashMap<>();
    data.put(CARD_TYPE, timelineSocialActionData.getCardType());
    data.put(ACTION, timelineSocialActionData.getAction());
    data.put(SOCIAL_ACTION, timelineSocialActionData.getSocialAction());
    data.put(PACKAGE, timelineSocialActionData.getPackageName());
    data.put(PUBLISHER, timelineSocialActionData.getPublisher());
    data.put(TITLE, timelineSocialActionData.getTitle());
    return data;
  }

  public void sendFollowFriendsEvent() {
    analyticsManager.logEvent(null, FOLLOW_FRIENDS, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return data;
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }

  public void sendRecommendedAppImpressionEvent(String packageName) {
    final Map<String, Object> data = new HashMap<>();
    data.put("type", "recommend app");
    data.put("fragment", getViewName(true));
    data.put("package_name", packageName);

    analyticsManager.logEvent(data, MESSAGE_IMPRESSION, AnalyticsManager.Action.IMPRESSION,
        navigationTracker.getViewName(true));
  }

  public void sendRecommendedAppInteractEvent(String packageName, String action) {
    final Map<String, Object> data = new HashMap<>();
    data.put("type", "recommend app");
    data.put("fragment", getViewName(true));
    data.put("package_name", packageName);
    data.put("action", action);

    analyticsManager.logEvent(data, MESSAGE_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }
}