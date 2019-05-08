package cm.aptoide.pt.home;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.ads.data.ApplicationAd;
import java.util.HashMap;
import java.util.Map;

import static cm.aptoide.analytics.AnalyticsManager.Action.OPEN;
import static cm.aptoide.pt.editorial.EditorialAnalytics.REACTION_INTERACT;

/**
 * Created by jdandrade on 28/03/2018.
 */

public class HomeAnalytics {

  public static final String HOME_INTERACT = "Home_Interact";
  public static final String HOME_CHIP_INTERACT = "Home_Chip_Interact";
  public static final String CURATION_CARD_IMPRESSION = "Curation_Card_Impression";
  public static final String CURATION_CARD_CLICK = "Curation_Card_Click";
  static final String SCROLL_RIGHT = "scroll right";
  static final String TAP_ON_APP = "tap on app";
  static final String IMPRESSION = "impression";
  static final String PULL_REFRESH = "pull refresh";
  static final String PUSH_LOAD_MORE = "push load more";
  static final String TAP_ON_MORE = "tap on more";
  static final String TAP_ON_CARD = "tap on card";
  static final String CHIP_CLICK = "chip";
  static final String CHIP_TAG = "chip_tag";
  static final String TAP_ON_CARD_DISMISS = "tap on card dismiss";
  static final String TAP = "tap";
  static final String VIEW_CARD = "view card";
  private static final String TAP_ON_CHIP = "tap_on_chip";
  private static final String WHERE = "where";
  private static final String PACKAGE_NAME = "package_name";
  private static final String ACTION = "action";
  private static final String BUNDLE_TAG = "bundle_tag";
  private static final String PROMOTION_ICON = "promotion-icon";
  private static final String PROMOTION_DIALOG = "promotion-dialog";
  private static final String CURATION_CARD = "curation_card";
  private final NavigationTracker navigationTracker;
  private final AnalyticsManager analyticsManager;

  public HomeAnalytics(NavigationTracker navigationTracker, AnalyticsManager analyticsManager) {
    this.navigationTracker = navigationTracker;
    this.analyticsManager = analyticsManager;
  }

  public void sendTapOnMoreInteractEvent(int bundlePosition, String bundleTag, int itemsInBundle) {
    sendTapOnMoreInteractEvent(bundlePosition, bundleTag, itemsInBundle, null);
  }

  public void sendTapOnMoreInteractEvent(int bundlePosition, String bundleTag, int itemsInBundle,
      String chipTag) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP_ON_MORE);
    data.put(BUNDLE_TAG, bundleTag);
    data.put("bundle_position", bundlePosition);
    data.put("bundle_total_items", itemsInBundle);
    if (chipTag != null) {
      data.put(CHIP_TAG, chipTag);
    }

    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendScrollRightInteractEvent(int bundlePosition, String bundleTag,
      int itemsInBundle) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, SCROLL_RIGHT);
    data.put(BUNDLE_TAG, bundleTag);
    data.put("bundle_position", bundlePosition);
    data.put("bundle_total_items", itemsInBundle);
    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.SCROLL,
        navigationTracker.getViewName(true));
  }

  public void sendLoadMoreInteractEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, PUSH_LOAD_MORE);
    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.ENDLESS_SCROLL,
        navigationTracker.getViewName(true));
  }

  public void sendPullRefreshInteractEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, PULL_REFRESH);
    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.PULL_REFRESH,
        navigationTracker.getViewName(true));
  }

  public void sendTapOnAppInteractEvent(double appRating, String packageName, int appPosition,
      int bundlePosition, String bundleTag, int itemsInBundle) {
    sendTapOnAppInteractEvent(appRating, packageName, appPosition, bundlePosition, bundleTag,
        itemsInBundle, null);
  }

  public void sendTapOnAppInteractEvent(double appRating, String packageName, int appPosition,
      int bundlePosition, String bundleTag, int itemsInBundle, String chipTag) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP_ON_APP);
    data.put("app_rating", appRating);
    data.put(PACKAGE_NAME, packageName);
    data.put("app_position", appPosition);
    data.put(BUNDLE_TAG, bundleTag);
    data.put("bundle_position", bundlePosition);
    data.put("bundle_total_items", itemsInBundle);
    if (chipTag != null) {
      data.put(CHIP_TAG, chipTag);
    }

    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  private void sendAdInteractEvent(String actionType, int appRating, String packageName,
      int bundlePosition, String bundleTag, HomeEvent.Type type, ApplicationAd.Network network) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, actionType);
    data.put("app_rating", appRating);
    data.put(PACKAGE_NAME, packageName);
    data.put(BUNDLE_TAG, bundleTag);
    data.put("bundle_position", bundlePosition);
    data.put("network", network.getName());

    analyticsManager.logEvent(data, HOME_INTERACT, parseAction(type),
        navigationTracker.getViewName(true));
  }

  public void sendAdClickEvent(int appRating, String packageName, int bundlePosition,
      String bundleTag, HomeEvent.Type type, ApplicationAd.Network network) {
    sendAdInteractEvent(TAP_ON_APP, appRating, packageName, bundlePosition, bundleTag, type,
        network);
  }

  public void sendActionItemTapOnCardInteractEvent(String bundleTag, int bundlePosition) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP_ON_CARD);
    data.put(BUNDLE_TAG, bundleTag);
    data.put("bundle_position", bundlePosition);

    analyticsManager.logEvent(data, HOME_INTERACT, OPEN, navigationTracker.getViewName(true));
  }

  public void sendActionItemEditorialTapOnCardInteractEvent(String bundleTag, int bundlePosition,
      String cardId) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP_ON_CARD);
    data.put(BUNDLE_TAG, bundleTag);
    data.put("bundle_position", bundlePosition);
    data.put("card_id", cardId);

    analyticsManager.logEvent(data, HOME_INTERACT, OPEN, navigationTracker.getViewName(true));
  }

  public void sendActionItemDismissInteractEvent(String bundleTag, int bundlePosition) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP_ON_CARD_DISMISS);
    data.put(BUNDLE_TAG, bundleTag);
    data.put("bundle_position", bundlePosition);

    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.DISMISS,
        navigationTracker.getViewName(true));
  }

  public void sendActionItemImpressionEvent(String bundleTag, int bundlePosition) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, VIEW_CARD);
    data.put(BUNDLE_TAG, bundleTag);
    data.put("bundle_position", bundlePosition);

    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.IMPRESSION,
        navigationTracker.getViewName(true));
  }

  public void sendEditorialInteractEvent(String bundleTag, int bundlePosition, String cardId) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP_ON_CARD);
    data.put(BUNDLE_TAG, bundleTag);
    data.put("card_id", cardId);
    data.put("bundle_position", bundlePosition);

    analyticsManager.logEvent(data, CURATION_CARD_CLICK, OPEN, navigationTracker.getViewName(true));
  }

  public void sendEditorialImpressionEvent(String bundleTag, int bundlePosition, String cardId) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, IMPRESSION);
    data.put(BUNDLE_TAG, bundleTag);
    data.put("card_id", cardId);
    data.put("bundle_position", bundlePosition);

    analyticsManager.logEvent(data, CURATION_CARD_IMPRESSION, AnalyticsManager.Action.IMPRESSION,
        navigationTracker.getViewName(true));
  }

  public void sendActionItemEditorialImpressionEvent(String bundleTag, int bundlePosition,
      String cardId) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, VIEW_CARD);
    data.put(BUNDLE_TAG, bundleTag);
    data.put("bundle_position", bundlePosition);
    data.put("card_id", cardId);

    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.IMPRESSION,
        navigationTracker.getViewName(true));
  }

  public void sendPromotionsIconClickEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP);
    data.put(BUNDLE_TAG, PROMOTION_ICON);

    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendPromotionsDialogImpressionEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, IMPRESSION);
    data.put(BUNDLE_TAG, PROMOTION_DIALOG);

    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.IMPRESSION,
        navigationTracker.getViewName(true));
  }

  public void sendPromotionsDialogDismissEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP_ON_CARD_DISMISS);
    data.put(BUNDLE_TAG, PROMOTION_DIALOG);

    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.DISMISS,
        navigationTracker.getViewName(true));
  }

  public void sendPromotionsDialogNavigateEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP_ON_CARD);
    data.put(BUNDLE_TAG, PROMOTION_DIALOG);

    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendPromotionsImpressionEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, IMPRESSION);
    data.put(BUNDLE_TAG, PROMOTION_ICON);

    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.IMPRESSION,
        navigationTracker.getViewName(true));
  }

  public void sendChipHomeInteractEvent(String chipTag) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, CHIP_CLICK);
    data.put(BUNDLE_TAG, chipTag);
    analyticsManager.logEvent(data, HOME_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendChipInteractEvent(String chipTag) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, TAP_ON_CHIP);
    data.put(CHIP_TAG, chipTag);
    analyticsManager.logEvent(data, HOME_CHIP_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendChipTapOnMore(String bundleTag, String chipTag) {
    analyticsManager.logEvent(createChipTapInteractMap(TAP_ON_MORE, bundleTag, chipTag),
        HOME_CHIP_INTERACT, AnalyticsManager.Action.CLICK, navigationTracker.getViewName(true));
  }

  public void sendChipTapOnApp(String bundleTag, String packageName, String chipTag) {
    final Map<String, Object> data = createChipTapInteractMap(TAP_ON_APP, bundleTag, chipTag);
    data.put(PACKAGE_NAME, packageName);
    analyticsManager.logEvent(data, HOME_CHIP_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public Map<String, Object> createChipTapInteractMap(String action, String bundleTag,
      String chipTag) {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, action);
    data.put(BUNDLE_TAG, bundleTag);
    data.put(CHIP_TAG, chipTag);
    return data;
  }

  public void convertAppcAdClick(String clickUrl) {
    analyticsManager.logEvent(clickUrl);
  }

  private AnalyticsManager.Action parseAction(HomeEvent.Type type) {
    if (type.equals(HomeEvent.Type.AD)) {
      return OPEN;
    }
    throw new IllegalStateException("TYPE " + type.name() + " NOT VALID");
  }

  public void sendReactionButtonClickEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "view_reactions");
    data.put(WHERE, CURATION_CARD);
    analyticsManager.logEvent(data, REACTION_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendReactedEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "click_to_react");
    data.put(WHERE, CURATION_CARD);
    analyticsManager.logEvent(data, REACTION_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendDeleteEvent() {
    final Map<String, Object> data = new HashMap<>();
    data.put(ACTION, "delete_reaction");
    data.put(WHERE, CURATION_CARD);
    analyticsManager.logEvent(data, REACTION_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }
}
