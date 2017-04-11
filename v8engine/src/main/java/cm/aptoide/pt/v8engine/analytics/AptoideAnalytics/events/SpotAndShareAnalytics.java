package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import cm.aptoide.pt.spotandshareandroid.analytics.SpotAndShareAnalyticsInterface;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 03/03/17.
 */

public class SpotAndShareAnalytics implements SpotAndShareAnalyticsInterface {

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
  public static String ACTION_SPOT_SHARE_PERM_GRANTED = "Permission granted";
  public static String ACTION_SPOT_SHARE_PERM_DENIED = "Permission not granted";
  private static String EVENT_NAME_SPOT_SHARE = "Share_Apps_Click_On_Share_Apps";
  private static String EVENT_NAME_SPOT_SHARE_PERMISSIONS = "Spot_Share_Write_Permissions_Problem";

  public SpotAndShareAnalytics() {
  }

  public static void clickShareApps() {
    trackEvent(EVENT_NAME_SPOT_SHARE, null, false);
  }

  public static void trackEvent(String eventName, Map<String, String> attributes, boolean fabric) {
    Analytics.getInstance().sendSpotAndShareEvents(eventName, attributes, fabric);
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
  public static void transferClick(String eventName, String action, boolean fabric) {
    //TODO this is called in the wrong place
    Map<String, String> attributes = new HashMap<>();
    attributes.put("action", action);
    trackEvent(eventName, attributes, fabric);
  }

  /**
   * Method to register the clicks that represent interactions with group creation or join group.
   *
   * @param eventName Should be either "Join Group" or "Create Group"
   * @param result In both cases should be Success or Unsuccessful
   */
  public static void groupClick(String eventName, String result, boolean fabric) {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("results", result);
    trackEvent(eventName, attributes, fabric);
  }

  @Override public void joinGroupSuccess() {
    groupClick(EVENT_NAME_SPOT_SHARE_JOIN, ACTION_SPOT_SHARE_SUCCESS, false);
  }

  @Override public void createGroupSuccess() {
    groupClick(EVENT_NAME_SPOT_SHARE_CREATE, ACTION_SPOT_SHARE_SUCCESS, false);
  }

  @Override public void createGroupFailed() {
    groupClick(EVENT_NAME_SPOT_SHARE_CREATE, ACTION_SPOT_SHARE_UNSUCCESS, false);
  }

  @Override public void joinGroupFailed() {
    groupClick(EVENT_NAME_SPOT_SHARE_JOIN, ACTION_SPOT_SHARE_UNSUCCESS, false);
  }

  @Override public void sendApkSuccess() {
    transferClick(EVENT_NAME_SPOT_SHARE_SEND_APP, ACTION_SPOT_SHARE_SEND_SUCCESS, false);
  }

  @Override public void sendApkFailed() {
    transferClick(EVENT_NAME_SPOT_SHARE_SEND_APP, ACTION_SPOT_SHARE_SEND_UNSUCCESS, false);
  }

  @Override public void receiveApkSuccess() {
    transferClick(EVENT_NAME_SPOT_SHARE_RECEIVE_APP, ACTION_SPOT_SHARE_RCV_SUCCESS, false);
  }

  @Override public void receiveApkFailed() {
    transferClick(EVENT_NAME_SPOT_SHARE_RECEIVE_APP, ACTION_SPOT_SHARE_RCV_UNSUCCESS, false);
  }

  /**
   * This event should only be sent to fabric, hence the third argument being true
   */
  @Override public void specialSettingsDenied() {
    groupClick(EVENT_NAME_SPOT_SHARE_PERMISSIONS, ACTION_SPOT_SHARE_PERM_DENIED, true);
  }

  /**
   * This event should only be sent to fabric, hence the third argument being true
   */
  @Override public void specialSettingsGranted() {
    groupClick(EVENT_NAME_SPOT_SHARE_PERMISSIONS, ACTION_SPOT_SHARE_PERM_GRANTED, true);
  }
}
