package cm.aptoide.pt.editorialList;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import java.util.HashMap;
import java.util.Map;

import static cm.aptoide.analytics.AnalyticsManager.Action.OPEN;

public class EditorialListAnalytics {

  public static final String CURATION_CARD_CLICK = "Curation_Card_Click";
  static final String TAP_ON_CARD = "tap on card";
  private static final String ACTION = "action";
  private static final String BUNDLE_TAG = "bundle_tag";
  private final NavigationTracker navigationTracker;
  private final AnalyticsManager analyticsManager;

  public EditorialListAnalytics(NavigationTracker navigationTracker,
      AnalyticsManager analyticsManager) {
    this.navigationTracker = navigationTracker;
    this.analyticsManager = analyticsManager;
  }

  public void sendEditorialInteractEvent(String cardId) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP_ON_CARD);
    data.put("card_id", cardId);

    analyticsManager.logEvent(data, CURATION_CARD_CLICK, OPEN, navigationTracker.getViewName(true));
  }
}
