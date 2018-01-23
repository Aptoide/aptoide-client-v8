package cm.aptoide.pt;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 17/04/17.
 */

public class DrawerAnalytics {

  public static final String DRAWER_OPEN_EVENT = "Drawer_Opened";
  public static final String DRAWER_INTERACT_EVENT = "Drawer_Interact";
  private static final String DEFAULT_CONTEXT = "Drawer";
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;

  public DrawerAnalytics(AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void drawerOpen() {
    analyticsManager.logEvent(new HashMap<>(), DRAWER_OPEN_EVENT, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  public void drawerInteract(String origin) {
    analyticsManager.logEvent(createMapData("action", origin), DRAWER_INTERACT_EVENT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return data;
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent, DEFAULT_CONTEXT);
  }
}
