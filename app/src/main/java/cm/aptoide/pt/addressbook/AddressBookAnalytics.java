package cm.aptoide.pt.addressbook;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcelobenites on 03/03/17.
 */

public class AddressBookAnalytics {

  public static final String HAS_NEW_CONNECTIONS_SCREEN = "Has New Connections";
  public static final String NO_NEW_CONNECTIONS_SCREEN = "No New Connections";
  public static final String NOT_ABLE_TO_CONNECT_SCREEN = "Not Able to Connect";
  private final Analytics analytics;
  private final AppEventsLogger facebook;

  public AddressBookAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void sendSyncFacebookEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_Choose_Network",
        createBundleData("choose_network_action", "Facebook")));
  }

  public void sendSyncTwitterEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_Choose_Network",
        createBundleData("choose_network_action", "Twitter")));
  }

  public void sendSyncAddressBookEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_Choose_Network",
        createBundleData("choose_network_action", "Sync Address Book")));
  }

  public void sendHowAptoideUsesYourDataEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_How_To"));
  }

  public void sendAllowAptoideAccessToContactsEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_Aptoide_Access",
        createBundleData("action", "Allow")));
  }

  public void sendDenyAptoideAccessToContactsEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_Aptoide_Access",
        createBundleData("action", "Deny")));
  }

  public void sendNewConnectionsAllowFriendsToFindYouEvent(String screen) {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_New_Connections",
        createScreenBundleData("action", "Allow friend to find you", screen)));
  }

  public void sendNewConnectionsDoneEvent(String screen) {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_New_Connections",
        createScreenBundleData("action", "Done", screen)));
  }

  public void sendNewConnectionsShareEvent(String screen) {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_New_Connections",
        createScreenBundleData("action", "Share", screen)));
  }

  public void sendShareYourPhoneSuccessEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_Set_My_Phonenumber"));
  }

  private Map<String, String> createScreenMapData(String key, String value, String screen) {
    final Map<String, String> data = createMapData(key, value);
    data.put("screen", screen);
    return data;
  }

  private Bundle createScreenBundleData(String key, String value, String screen) {
    final Bundle data = createBundleData(key, value);
    data.putString("screen", screen);
    return data;
  }

  private Bundle createBundleData(String key, String value) {
    final Bundle data = new Bundle();
    data.putString(key, value);
    return data;
  }

  private Map<String, String> createMapData(String key, String value) {
    final Map<String, String> data = new HashMap<>();
    data.put(key, value);
    return data;
  }
}
