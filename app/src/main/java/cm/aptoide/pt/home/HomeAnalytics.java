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
  private final NavigationTracker navigationTracker;
  private final AnalyticsManager analyticsManager;

  public HomeAnalytics(NavigationTracker navigationTracker, AnalyticsManager analyticsManager) {
    this.navigationTracker = navigationTracker;
    this.analyticsManager = analyticsManager;
  }

  public void sendRecommendedAppInteractEvent(double appRating, String packageName, int position,
      AppClick.Type type) {
    final Map<String, Object> data = new HashMap<>();
    data.put("app_rating", appRating);
    data.put("package_name", packageName);
    data.put("section_name", "recommendation card");
    data.put("section_position", position);

    analyticsManager.logEvent(data, HOME_INTERACT, parseAction(type),
        navigationTracker.getViewName(true));
  }

  private AnalyticsManager.Action parseAction(AppClick.Type type) {
    if (type.equals(AppClick.Type.SOCIAL_CLICK)) {
      return AnalyticsManager.Action.OPEN;
    } else if (type.equals(AppClick.Type.SOCIAL_INSTALL)) {
      return AnalyticsManager.Action.INSTALL;
    }
    throw new IllegalStateException("TYPE " + type.name() + " NOT VALID");
  }
}
