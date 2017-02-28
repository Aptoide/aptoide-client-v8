package cm.aptoide.lite.localytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by filipegoncalves on 18-11-2016.
 */

public class AnalyticsLite {

  // public static LocalyticsSession localyticsSession;
  public static final String LITE_CUSTOM_DIMENSION = "lite";
  public static final int LITE_CUSTOM_DIMENSION_VERTICAL = 2;

  public static void clickShareApps() {
    trackEvent("Click on Share Apps", null);
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
    attributes.put("Result", result);
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
    Map<String, String> attributes = new HashMap<>();
    attributes.put("Action", action);
    trackEvent(eventName, attributes);
  }
}
