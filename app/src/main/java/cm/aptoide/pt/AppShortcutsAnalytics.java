package cm.aptoide.pt;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by franciscocalado on 1/24/18.
 */

public class AppShortcutsAnalytics {

  public static final String APPS_SHORTCUTS = "Apps_Shortcuts";
  private static final String DESTINATION = "destination";
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;

  public AppShortcutsAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void shortcutNavigation(String destination) {
    Map<String, Object> map = new HashMap<>();
    map.put(DESTINATION, destination);

    analyticsManager.logEvent(map, APPS_SHORTCUTS, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }

}
