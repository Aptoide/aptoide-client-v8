package cm.aptoide.pt.store;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 26/06/17.
 */

public class StoreAnalytics {

  public static final String STORES_TAB_INTERACT = "Stores_Tab_Interact";
  public static final String STORES_OPEN = "Store_Open";
  public static final String STORES_INTERACT = "Store_Interact";
  private static final String ACTION = "action";
  private static final String SOURCE = "source";
  private static final String STORE_NAME = "store_name";
  private static final String FOLLOW_STORE_APPS = "follow_store_apps";
  private static final String FOLLOW_STORE_FOLLOWERS = "follow_store_followers";
  private static final String TAB = "tab_name";
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;

  public StoreAnalytics(AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  /// "add store" event implemented (according to sunil the information about how many apps/subscribers only needs to be sent when comming from a "follow a recommended store" event
  public void sendStoreTabInteractEvent(String action, boolean isCurrent) {
    analyticsManager.logEvent(createStoreInteractMap(action), STORES_TAB_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(isCurrent));
  }

  //Only for "follow a recommended store event"
  public void sendStoreTabInteractEvent(String action, int storeAppsNumber, int storeFollowers) {
    analyticsManager.logEvent(
        createStoreTabInteractDataMap(action, storeAppsNumber, storeFollowers), STORES_TAB_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendStoreOpenEvent(String source, String storeName, boolean isCurrent) {
    analyticsManager.logEvent(createStoreOpenDataMap(source, storeName), STORES_OPEN,
        AnalyticsManager.Action.CLICK, getViewName(isCurrent));
  }

  public void sendStoreInteractEvent(String action, String tab, String storeName) {
    analyticsManager.logEvent(createStoreInteractDataMap(action, tab, storeName), STORES_INTERACT,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createStoreInteractDataMap(String action, String tab,
      String storeName) {
    Map<String, Object> map = new HashMap<>();
    map.put(ACTION, action);
    map.put(TAB, tab);
    map.put(STORE_NAME, storeName);
    return map;
  }

  private Map<String, Object> createStoreInteractMap(String action) {
    Map<String, Object> map = new HashMap<>();
    map.put(ACTION, action);
    return map;
  }

  private Map<String, Object> createStoreOpenDataMap(String source, String storeName) {
    Map<String, Object> map = new HashMap<>();
    map.put(SOURCE, source);
    map.put(STORE_NAME, storeName);
    return map;
  }

  private Map<String, Object> createStoreTabInteractDataMap(String action, int storeAppsNumber,
      int storeFollowers) {
    Map<String, Object> map = new HashMap<>();
    map.put(ACTION, action);
    map.put(FOLLOW_STORE_APPS, AptoideUtils.StringU.toString(storeAppsNumber));
    map.put(FOLLOW_STORE_FOLLOWERS, AptoideUtils.StringU.toString(storeFollowers));
    return map;
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }
}
