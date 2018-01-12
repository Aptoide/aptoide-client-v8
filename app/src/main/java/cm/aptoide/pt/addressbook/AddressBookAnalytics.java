package cm.aptoide.pt.addressbook;

import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcelobenites on 03/03/17.
 */

public class AddressBookAnalytics {

  public static final String HAS_NEW_CONNECTIONS_SCREEN = "Has New Connections";
  public static final String FOLLOW_FRIENDS_CHOOSE_NETWORK = "Follow_Friends_Choose_Network";
  public static final String FOLLOW_FRIENDS_HOW_TO = "Follow_Friends_How_To";
  public static final String FOLLOW_FRIENDS_APTOIDE_ACCESS = "Follow_Friends_Aptoide_Access";
  public static final String FOLLOW_FRIENDS_NEW_CONNECTIONS = "Follow_Friends_New_Connections";
  public static final String FOLLOW_FRIENDS_SET_MY_PHONENUMBER = "Follow_Friends_Set_My_Phonenumber";
  private static final String CHOOSE_NETWORK_ACTION = "choose_network_action";
  private static final String ACTION = "action";
  private final AnalyticsManager analyticsManager;

  public AddressBookAnalytics(AnalyticsManager analyticsManager) {
    this.analyticsManager = analyticsManager;
  }

  public void sendSyncFacebookEvent(String context) {
    analyticsManager.logEvent(createMapData(CHOOSE_NETWORK_ACTION,"Facebook"),FOLLOW_FRIENDS_CHOOSE_NETWORK,AnalyticsManager.Action.CLICK,context);
  }

  public void sendSyncTwitterEvent(String context) {
    analyticsManager.logEvent(createMapData(CHOOSE_NETWORK_ACTION,"Twitter"),FOLLOW_FRIENDS_CHOOSE_NETWORK,
        AnalyticsManager.Action.INSTALL,context);
  }

  public void sendSyncAddressBookEvent(String context) {
    analyticsManager.logEvent(createMapData(CHOOSE_NETWORK_ACTION,"Sync Address Book"),FOLLOW_FRIENDS_CHOOSE_NETWORK,
        AnalyticsManager.Action.CLICK, context);
  }

  public void sendHowAptoideUsesYourDataEvent(String context) {
    analyticsManager.logEvent(null, FOLLOW_FRIENDS_HOW_TO,AnalyticsManager.Action.OPEN,context);
  }

  public void sendAllowAptoideAccessToContactsEvent(String context) {
    analyticsManager.logEvent(createMapData(ACTION,"Allow"),FOLLOW_FRIENDS_APTOIDE_ACCESS,
        AnalyticsManager.Action.CLICK,context);
  }

  public void sendDenyAptoideAccessToContactsEvent(String context) {
    analyticsManager.logEvent(createMapData(ACTION,"Deny"),FOLLOW_FRIENDS_APTOIDE_ACCESS,
        AnalyticsManager.Action.OPEN, context);
  }

  public void sendNewConnectionsAllowFriendsToFindYouEvent(String screen, String context) {
    Map<String, Object> data = createMapData(ACTION,"Allow friend to find you");
    data.put("screen",screen);
    analyticsManager.logEvent(data,FOLLOW_FRIENDS_NEW_CONNECTIONS, AnalyticsManager.Action.CLICK,context);
  }

  public void sendNewConnectionsDoneEvent(String screen, String context) {
    Map<String, Object> data = createMapData(ACTION,"Done");
    data.put("screen",screen);
    analyticsManager.logEvent(data,FOLLOW_FRIENDS_NEW_CONNECTIONS, AnalyticsManager.Action.CLICK, context);
  }

  public void sendNewConnectionsShareEvent(String screen, String context) {
    Map<String, Object> data = createMapData(ACTION,"Share");
    data.put("screen",screen);
    analyticsManager.logEvent(data,FOLLOW_FRIENDS_NEW_CONNECTIONS, AnalyticsManager.Action.CLICK, context);
  }

  public void sendShareYourPhoneSuccessEvent(String context) {
    analyticsManager.logEvent(null,FOLLOW_FRIENDS_SET_MY_PHONENUMBER, AnalyticsManager.Action.INPUT,context);
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return data;
  }
}
