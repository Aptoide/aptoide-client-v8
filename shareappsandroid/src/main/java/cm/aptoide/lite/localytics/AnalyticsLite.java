package cm.aptoide.lite.localytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by filipegoncalves on 18-11-2016.
 */

public class AnalyticsLite {

  private static String EVENT_NAME_SPOT_SHARE = "Share_Apps_Click_On_Share_Apps";
  public static String EVENT_NAME_SPOT_SHARE_JOIN = "Share_Apps_Join_Group";
  public static String EVENT_NAME_SPOT_SHARE_CREATE = "Share_Apps_Create_Group";
  public static String EVENT_NAME_SPOT_SHARE_SEND_APP = "Share_Apps_Send_App";
  public static String EVENT_NAME_SPOT_SHARE_RECEIVE_APP = "Share_Apps_Receive_App";

  public static String ACTION_SPOT_SHARE_SUCCESS = "Success";
  public static String ACTION_SPOT_SHARE_UNSUCCESS = "Unsuccessful";

  public static String ACTION_SPOT_SHARE_SEND_APP = "Send app";
  public static String ACTION_SPOT_SHARE_SEND_SUCCESS = "Success send";
  public static String ACTION_SPOT_SHARE_SEND_UNSUCCESS = "Unsuccessful send";

  public static String ACTION_SPOT_SHARE_RCV_SUCCESS = "Success received";
  public static String ACTION_SPOT_SHARE_RCV_UNSUCCESS = "Unsuccessful received";

  public static void clickShareApps() {
    //TODO THis should be called in v8engine, not here
    //TODO click on drawer and click on start btn in SpotShare Tab
    trackEvent(EVENT_NAME_SPOT_SHARE, null);
  }

  public static void trackEvent(String eventName, Map<String, String> attributes) {

  }

  /**
   * Method to register the clicks that represent interactions with group creation or join group.
   *
   * @param eventName Should be either "Join Group" or "Create Group"
   * @param result In both cases should be Success or Unsuccessful
   */
  public static void groupClick(String eventName, String result) {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("results", result);
    trackEvent(eventName, attributes);
  }

  /**
   * Method to register the transfer events.
   *
   * @param eventName should be either "Send App" or "Receive App"
   * @param action In case the event is a Send App, this action can be "Send app", "Successful
   * send", "Unsuccessful send".
   * If the event is a Receive App, this action can be either "Successful received", "Unsuccessful
   * received"
   */
  public static void transferClick(String eventName, String action) {
    //TODO this is called in the wrong place
    Map<String, String> attributes = new HashMap<>();
    attributes.put("action", action);
    trackEvent(eventName, attributes);
  }
}
