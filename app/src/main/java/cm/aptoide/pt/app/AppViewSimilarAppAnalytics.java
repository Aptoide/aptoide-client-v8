package cm.aptoide.pt.app;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.ads.model.ApplicationAd;
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
  private static final String NETWORK = "Network";
  private static final String IS_AD = "Is_ad";
  private static final String POSITION = "Position";
  private static final String PACKAGE_NAME = "Package_name";
  private static final String ACTION = "Action";

  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;

  public AppViewSimilarAppAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void similarAppBundleImpression(ApplicationAd.Network network, boolean isAd) {
    similarAppInteract(network, Action.IMPRESSION, null, -1, isAd);
  }

  public void similarAppClick(ApplicationAd.Network network, String packageName, int position,
      boolean isAd) {
    similarAppInteract(network, Action.TAP_ON_APP, packageName, position, isAd);
  }

  private void similarAppInteract(ApplicationAd.Network network, Action action, String packageName,
      int position, boolean isAd) {
    Map<String, Object> data = new HashMap<>();
    if (isAd) data.put(NETWORK, network.getName());
    data.put(ACTION, action.getName());
    data.put(IS_AD, isAd ? "true" : "false");
    if (action == Action.TAP_ON_APP) data.put(PACKAGE_NAME, packageName);
    if (action == Action.TAP_ON_APP) data.put(POSITION, position);

    analyticsManager.logEvent(data, SIMILAR_APP_INTERACT,
        action == Action.IMPRESSION ? AnalyticsManager.Action.IMPRESSION
            : AnalyticsManager.Action.CLICK, navigationTracker.getViewName(true));
    Logger.getInstance()
        .w(TAG, "Facebook Event: " + SIMILAR_APP_INTERACT + " : " + data.toString());
  }

  public enum Action {
    IMPRESSION("impression"), TAP_ON_APP("tap_on_app");
    private String name;

    Action(String network) {
      this.name = network;
    }

    public String getName() {
      return name;
    }
  }
}
