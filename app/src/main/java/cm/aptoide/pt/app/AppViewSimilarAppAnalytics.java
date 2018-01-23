package cm.aptoide.pt.app;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.logger.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 10/05/17.
 */

// Open App View

public class AppViewSimilarAppAnalytics {

  public static final String APP_VIEW_SIMILAR_APP_SLIDE_IN = "App_View_Similar_App_Slide_In";
  public static final String SIMILAR_APP_INTERACT = "Similar_App_Interact";
  private static final String TAG = AppViewSimilarAppAnalytics.class.getSimpleName();
  private static final String ACTION = "Action";
  private static final String DEFAULT_CONTEXT = "AppViewSimilarApp";
  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;

  public AppViewSimilarAppAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void similarAppsIsShown() {

    analyticsManager.logEvent(new HashMap<>(), APP_VIEW_SIMILAR_APP_SLIDE_IN,
        AnalyticsManager.Action.CLICK, getViewName(true));
    Logger.w(TAG, "Facebook Event: " + APP_VIEW_SIMILAR_APP_SLIDE_IN);
  }

  public void openSimilarApp() {
    Map<String, Object> parameters = createMapData(ACTION, "Open App View");
    analyticsManager.logEvent(parameters, SIMILAR_APP_INTERACT, AnalyticsManager.Action.CLICK,
        getViewName(true));
    Logger.w(TAG, "Facebook Event: " + SIMILAR_APP_INTERACT + " : " + parameters.toString());
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
