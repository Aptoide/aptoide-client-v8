package cm.aptoide.pt.v8engine.store;

import android.os.Bundle;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import cm.aptoide.pt.v8engine.analytics.events.FlurryEvent;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 26/06/17.
 */

public class StoreAnalytics {

  private static final String STORES_TAB_OPEN = "Stores_Tab_Open";
  private static final String STORES_TAB_INTERACT = "Stores_Tab_Interact";
  private static final String ACTION = "action";
  private static final String FOLLOW_STORE_APPS = "follow_store_apps";
  private static final String FOLLOW_STORE_FOLLOWERS = "follow_store_followers";
  private final AppEventsLogger facebook;
  private final Analytics analytics;

  public StoreAnalytics(AppEventsLogger facebook, Analytics analytics) {
    this.facebook = facebook;
    this.analytics = analytics;
  }

  public void sendStoreTabOpenedEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, STORES_TAB_OPEN));
    analytics.sendEvent(new FlurryEvent(STORES_TAB_OPEN));
  }

  public void sendStoreInteractEvent(String action, int storeAppsNumber, int storeFollowers) {
    analytics.sendEvent(new FacebookEvent(facebook, STORES_TAB_INTERACT,
        createStoreInteractFacebookBundle(action, storeAppsNumber, storeFollowers)));
    analytics.sendEvent(new FlurryEvent(STORES_TAB_INTERACT,
        createStoreInteractFlurryDataMap(action, storeAppsNumber, storeFollowers)));
  }

  private Map<String, String> createStoreInteractFlurryDataMap(String action, int storeAppsNumber,
      int storeFollowers) {
    Map<String, String> map = new HashMap<>();
    map.put(ACTION, action);
    map.put(FOLLOW_STORE_APPS, AptoideUtils.StringU.toString(storeAppsNumber));
    map.put(FOLLOW_STORE_FOLLOWERS, AptoideUtils.StringU.toString(storeFollowers));
    return map;
  }

  private Bundle createStoreInteractFacebookBundle(String action, int storeAppsNumber,
      int storeFollowers) {
    Bundle bundle = new Bundle();
    bundle.putString(ACTION, action);
    bundle.putInt(FOLLOW_STORE_APPS, storeAppsNumber);
    bundle.putInt(FOLLOW_STORE_FOLLOWERS, storeFollowers);
    return bundle;
  }
}
