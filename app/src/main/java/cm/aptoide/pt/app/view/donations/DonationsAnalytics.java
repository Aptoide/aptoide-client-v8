package cm.aptoide.pt.app.view.donations;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import java.util.HashMap;

public class DonationsAnalytics {

  public static final String DONATIONS_INTERACT = "Donations_Dialog_Interact";
  public static final String ACTION = "Action";
  public static final String PACKAGE_NAME = "package_name";
  public static final String VALUE = "value";
  public static final String NAME = "name";

  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;

  public DonationsAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void sendDonateInteractEvent(String packageName, float value, boolean hasNickname) {
    analyticsManager.logEvent(createDonationsInteractMap("donate", packageName, value, hasNickname),
        DONATIONS_INTERACT, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendCancelInteractEvent(String packageName, float value, boolean hasNickname) {
    analyticsManager.logEvent(createDonationsInteractMap("cancel", packageName, value, hasNickname),
        DONATIONS_INTERACT, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private HashMap<String, Object> createDonationsInteractMap(String action, String packageName,
      float value, boolean hasNickname) {
    HashMap<String, Object> map = new HashMap<>();
    map.put(ACTION, action);
    map.put(PACKAGE_NAME, packageName);
    map.put(VALUE, value);
    map.put(NAME, hasNickname);
    return map;
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }
}
