package cm.aptoide.pt.updates;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.app.AppViewAnalytics;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 18/04/17.
 */

public class UpdatesAnalytics {

  public final static String UPDATE_EVENT = "Updates";
  public final static String OPEN_APP_VIEW = "Open App View";
  public final static String OPEN_APP_VIEW_MIGRATIOM = "Open Migrated App View";
  private static final String TYPE = "type";
  private static final String APPLICATION_NAME = "Application Name";
  private static final String CONTEXT = "context";
  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;

  public UpdatesAnalytics(AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void updates(String action) {
    analyticsManager.logEvent(createMapData("action", action), UPDATE_EVENT,
        AnalyticsManager.Action.AUTO, navigationTracker.getViewName(true));
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return data;
  }

  public void sendUpdateClickedEvent(String packageName) {
    String context = navigationTracker.getViewName(true);
    HashMap<String, Object> map = new HashMap<>();
    map.put(TYPE, "UPDATE");
    map.put(APPLICATION_NAME, packageName);
    map.put(CONTEXT, context);
    analyticsManager.logEvent(map, AppViewAnalytics.CLICK_INSTALL, AnalyticsManager.Action.CLICK,
        context);
  }

  public void sendUpdateAllClickEvent() {
    String context = navigationTracker.getViewName(true);
    HashMap<String, Object> map = new HashMap<>();
    map.put(TYPE, "update all");
    map.put(CONTEXT, context);
    analyticsManager.logEvent(map, AppViewAnalytics.CLICK_INSTALL, AnalyticsManager.Action.CLICK,
        context);
  }
}
