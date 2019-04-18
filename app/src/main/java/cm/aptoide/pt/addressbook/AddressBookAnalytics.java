package cm.aptoide.pt.addressbook;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcelobenites on 03/03/17.
 */

public class AddressBookAnalytics {

  public static final String HAS_NEW_CONNECTIONS_SCREEN = "Has New Connections";
  public static final String FOLLOW_FRIENDS_NEW_CONNECTIONS = "Follow_Friends_New_Connections";
  public static final String FOLLOW_FRIENDS_SET_MY_PHONENUMBER =
      "Follow_Friends_Set_My_Phonenumber";
  private static final String ACTION = "action";
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;

  public AddressBookAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void sendNewConnectionsAllowFriendsToFindYouEvent(String screen) {
    Map<String, Object> data = createMapData(ACTION, "Allow friend to find you");
    data.put("screen", screen);
    analyticsManager.logEvent(data, FOLLOW_FRIENDS_NEW_CONNECTIONS, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  public void sendNewConnectionsDoneEvent(String screen) {
    Map<String, Object> data = createMapData(ACTION, "Done");
    data.put("screen", screen);
    analyticsManager.logEvent(data, FOLLOW_FRIENDS_NEW_CONNECTIONS, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  public void sendShareYourPhoneSuccessEvent() {
    analyticsManager.logEvent(null, FOLLOW_FRIENDS_SET_MY_PHONENUMBER,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return data;
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }
}
