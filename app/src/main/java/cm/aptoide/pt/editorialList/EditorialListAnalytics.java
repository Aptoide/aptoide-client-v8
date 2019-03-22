package cm.aptoide.pt.editorialList;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import java.util.HashMap;
import java.util.Map;

import static cm.aptoide.analytics.AnalyticsManager.Action.OPEN;

public class EditorialListAnalytics {

  public static final String EDITORIAL_BN_CURATION_CARD_CLICK = "Editorial_BN_Curation_Card_Click";
  public static final String EDITORIAL_BN_CURATION_CARD_IMPRESSION =
      "Editorial_BN_Curation_Card_Impression";
  static final String IMPRESSION = "impression";
  static final String TAP_ON_CARD = "tap on card";
  private static final String ACTION = "action";
  private final NavigationTracker navigationTracker;
  private final AnalyticsManager analyticsManager;

  public EditorialListAnalytics(NavigationTracker navigationTracker,
      AnalyticsManager analyticsManager) {
    this.navigationTracker = navigationTracker;
    this.analyticsManager = analyticsManager;
  }

  public void sendEditorialInteractEvent(String cardId, int position) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP_ON_CARD);
    data.put("card_id", cardId);
    data.put("position", position);

    analyticsManager.logEvent(data, EDITORIAL_BN_CURATION_CARD_CLICK, OPEN,
        navigationTracker.getViewName(true));
  }

  public void sendEditorialImpressionEvent(String cardId, int position) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, IMPRESSION);
    data.put("card_id", cardId);
    data.put("position", position);

    analyticsManager.logEvent(data, EDITORIAL_BN_CURATION_CARD_IMPRESSION,
        AnalyticsManager.Action.IMPRESSION, navigationTracker.getViewName(true));
  }

  public void sendReactionButtonClickEvent(String cardId, int bundlePosition) {
    //TODO
  }
}
