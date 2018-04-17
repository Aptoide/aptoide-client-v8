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
  public static final String TAP_ON_APP = "tap on app";
  private static final String TAP_ON_MORE = "tap on more";
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
      int bundlePosition, HomeClick.Type type) {
    final Map<String, Object> data = new HashMap<>();
    data.put("app_rating", appRating);
    data.put("package_name", packageName);
    data.put("bundle_name", "recommendation card");
    data.put("bundle_position", bundlePosition);

    analyticsManager.logEvent(data, HOME_INTERACT, parseAction(type),
        navigationTracker.getViewName(true));
  }

  private AnalyticsManager.Action parseAction(HomeClick.Type type) {
    if (type.equals(HomeClick.Type.SOCIAL_CLICK)) {
      return AnalyticsManager.Action.OPEN;
    } else if (type.equals(HomeClick.Type.SOCIAL_INSTALL)) {
      return AnalyticsManager.Action.INSTALL;
    }
    throw new IllegalStateException("TYPE " + type.name() + " NOT VALID");
  }
}
