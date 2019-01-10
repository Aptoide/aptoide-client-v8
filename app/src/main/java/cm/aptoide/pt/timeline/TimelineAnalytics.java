package cm.aptoide.pt.timeline;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jdandrade on 27/10/2016.
 */
public class TimelineAnalytics {

  public static final String SOCIAL_CARD_ACTION_SHARE_CONTINUE = "Continue";
  public static final String SOCIAL_CARD_ACTION_SHARE_CANCEL = "Cancel";
  public static final String STORE = "store";
  public static final String OPEN_ARTICLE = "OPEN_ARTICLE";
  public static final String OPEN_BLOG = "OPEN_BLOG";
  public static final String OPEN_VIDEO = "OPEN_VIDEO";
  public static final String OPEN_CHANNEL = "OPEN_CHANNEL";
  public static final String OPEN_STORE = "OPEN_STORE";
  public static final String OPEN_STORE_PROFILE = "OPEN_STORE_PROFILE";
  public static final String OPEN_APP = "OPEN_APP";
  public static final String OPEN_TIMELINE_EVENT = "OPEN_TIMELINE";
  public static final String UPDATE_APP = "UPDATE_APP";
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
  private static final String ACTION = "action";
  private final NavigationTracker navigationTracker;
  private final AnalyticsManager analyticsManager;

  public TimelineAnalytics(NavigationTracker navigationTracker, AnalyticsManager analyticsManager) {
    this.navigationTracker = navigationTracker;
    this.analyticsManager = analyticsManager;
  }

  public void sendSocialCardPreviewActionEvent(String value) {
    analyticsManager.logEvent(createMapData(ACTION, value), SOCIAL_CARD_PREVIEW,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put("alternative_flow", true);
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
    data.put("alternative_flow", true);

    analyticsManager.logEvent(data, MESSAGE_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }
}