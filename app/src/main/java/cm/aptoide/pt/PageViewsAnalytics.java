package cm.aptoide.pt;

import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

public class PageViewsAnalytics {

  public final static String PAGE_VIEW_EVENT = "Page_View";
  private final AnalyticsManager analyticsManager;

  public PageViewsAnalytics(AnalyticsManager analyticsManager) {
    this.analyticsManager = analyticsManager;
  }

  public void sendPageViewedEvent(String currentViewName, String previousViewName, String store) {
    analyticsManager.logEvent(createEventMap(currentViewName, store), PAGE_VIEW_EVENT,
        AnalyticsManager.Action.CLICK, previousViewName);
  }

  private Map<String, Object> createEventMap(String currentViewName, String store) {
    Map<String, Object> map = new HashMap<>();
    map.put("fragment", currentViewName);
    map.put("store", store);
    return map;
  }
}