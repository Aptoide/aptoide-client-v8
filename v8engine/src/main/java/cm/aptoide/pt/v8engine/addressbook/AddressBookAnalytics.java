package cm.aptoide.pt.v8engine.addressbook;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.FacebookEvent;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.LocalyticsEvent;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcelobenites on 03/03/17.
 */

public class AddressBookAnalytics {

  private final Analytics analytics;
  private final AppEventsLogger facebook;

  public AddressBookAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void sendSyncFacebookEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_Choose_Network",
        createBundleData("choose_network_action", "Facebook")));
    analytics.sendEvent(new LocalyticsEvent("Follow_Friends_Choose_Network",
        createMapData("choose_network_action", "Facebook")));
  }

  public void sendSyncTwitterEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_Choose_Network",
        createBundleData("choose_network_action", "Twitter")));
    analytics.sendEvent(new LocalyticsEvent("Follow_Friends_Choose_Network",
        createMapData("choose_network_action", "Twitter")));
  }

  public void sendSyncAddressBookEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_Choose_Network",
        createBundleData("choose_network_action", "Sync Address Book")));
    analytics.sendEvent(new LocalyticsEvent("Follow_Friends_Choose_Network",
        createMapData("choose_network_action", "Sync Address Book")));
  }

  public void sendHowAptoideUsesYourDataEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_How_To"));
    analytics.sendEvent(new LocalyticsEvent("Follow_Friends_How_To"));
  }

  public void sendAllowAptoideAccessToContactsEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_Aptoide_Access",
        createBundleData("action", "Allow")));
    analytics.sendEvent(new LocalyticsEvent("Follow_Friends_Aptoide_Access", createMapData
        ("action", "Allow")));
  }

  public void sendDenyAptoideAccessToContactsEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Follow_Friends_Aptoide_Access",
        createBundleData("action", "Deny")));
    analytics.sendEvent(new LocalyticsEvent("Follow_Friends_Aptoide_Access", createMapData
        ("action", "Deny")));
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
