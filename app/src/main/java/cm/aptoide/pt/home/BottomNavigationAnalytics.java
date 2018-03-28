package cm.aptoide.pt.home;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by franciscocalado on 27/03/18.
 */

public class BottomNavigationAnalytics {
  public static final String BOTTOM_NAVIGATION_INTERACT = "Bottom_Navigation_Interact";
  private static final String VIEW = "view";
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;

  public BottomNavigationAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void sendNavigateToHomeClickEvent() {
    analyticsManager.logEvent(createBottomNavData("home"), BOTTOM_NAVIGATION_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendNavigateToSearchClickEvent() {
    analyticsManager.logEvent(createBottomNavData("search"), BOTTOM_NAVIGATION_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendNavigateToStoresClickEvent() {
    analyticsManager.logEvent(createBottomNavData("stores"), BOTTOM_NAVIGATION_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendNavigateToAppsClickEvent() {
    analyticsManager.logEvent(createBottomNavData("apps"), BOTTOM_NAVIGATION_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createBottomNavData(String view) {
    Map<String, Object> map = new HashMap<>();
    map.put(VIEW, view);
    return map;
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }
}
