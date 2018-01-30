package cm.aptoide.pt.account;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

public class AdultContentAnalytics {
  public static final String ADULT_CONTENT = "Adult Content";
  public static final String UNLOCK = "false";
  public static final String LOCK = "true";

  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;

  public AdultContentAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void lock() {
    Map<String, Object> map = new HashMap<>();
    map.put("Action", LOCK);
    analyticsManager.logEvent(map, ADULT_CONTENT, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void unlock() {
    Map<String, Object> map = new HashMap<>();
    map.put("Action", UNLOCK);
    analyticsManager.logEvent(map, ADULT_CONTENT, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }
}
