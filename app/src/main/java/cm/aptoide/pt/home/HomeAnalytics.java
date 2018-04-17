package cm.aptoide.pt.home;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jdandrade on 28/03/2018.
 */

public class HomeAnalytics {

  public static final String HOME_INTERACT = "Home_Interact";
  static final String SCROLL_RIGHT = "scroll right";
  static final String TAP_ON_APP = "tap on app";
  static final String PULL_REFRESH = "pull refresh";
  static final String PUSH_LOAD_MORE = "push load more";
  static final String TAP_ON_MORE = "tap on more";
  private final NavigationTracker navigationTracker;
  private final AnalyticsManager analyticsManager;

  public HomeAnalytics(NavigationTracker navigationTracker, AnalyticsManager analyticsManager) {
    this.navigationTracker = navigationTracker;
    this.analyticsManager = analyticsManager;
  }

  public void sendTapOnMoreInteractEvent(int bundlePosition, String bundleName, int itemsInBundle) {
    final Map<String, Object> data = new HashMap<>();
    data.put("action", TAP_ON_MORE);
    data.put("bundle_name", bundleName);
    data.put("bundle_position", bundlePosition);
    data.put("bundle_total_items", itemsInBundle);

    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendScrollRightInteractEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put("action", SCROLL_RIGHT);
    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.SCROLL,
        navigationTracker.getViewName(true));
  }

  public void sendLoadMoreInteractEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put("action", PUSH_LOAD_MORE);
    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.ENDLESS_SCROLL,
        navigationTracker.getViewName(true));
  }

  public void sendPullRefreshInteractEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put("action", PULL_REFRESH);
    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.PULL_REFRESH,
        navigationTracker.getViewName(true));
  }

  public void sendTapOnAppInteractEvent(double appRating, String packageName, int appPosition,
      int bundlePosition, String bundleName, int itemsInBundle) {
    final Map<String, Object> data = new HashMap<>();
    data.put("action", TAP_ON_APP);
    data.put("app_rating", appRating);
    data.put("package_name", packageName);
    data.put("app_position", appPosition);
    data.put("bundle_name", bundleName);
    data.put("bundle_position", bundlePosition);
    data.put("bundle_total_items", itemsInBundle);

    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendRecommendedAppInteractEvent(double appRating, String packageName,
      int bundlePosition, HomeEvent.Type type) {
    final Map<String, Object> data = new HashMap<>();
    data.put("action", TAP_ON_APP);
    data.put("app_rating", appRating);
    data.put("package_name", packageName);
    data.put("bundle_name", "recommendation card");
    data.put("bundle_position", bundlePosition);

    analyticsManager.logEvent(data, HOME_INTERACT, parseAction(type),
        navigationTracker.getViewName(true));
  }

  private AnalyticsManager.Action parseAction(HomeEvent.Type type) {
    if (type.equals(HomeEvent.Type.SOCIAL_CLICK)) {
      return AnalyticsManager.Action.OPEN;
    } else if (type.equals(HomeEvent.Type.SOCIAL_INSTALL)) {
      return AnalyticsManager.Action.INSTALL;
    }
    throw new IllegalStateException("TYPE " + type.name() + " NOT VALID");
  }
}
