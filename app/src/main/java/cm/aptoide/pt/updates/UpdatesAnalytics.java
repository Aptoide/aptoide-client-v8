package cm.aptoide.pt.updates;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 18/04/17.
 */

public class UpdatesAnalytics {

  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;
  public final static String UPDATE_EVENT = "Updates";

  public UpdatesAnalytics(AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void updates(String action) {
    analyticsManager.logEvent(createMapData("action",action),UPDATE_EVENT, AnalyticsManager.Action.AUTO,navigationTracker.getCurrentViewName());
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return data;
  }
}
