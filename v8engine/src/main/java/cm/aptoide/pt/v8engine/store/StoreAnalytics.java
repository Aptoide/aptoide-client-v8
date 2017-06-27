package cm.aptoide.pt.v8engine.store;

import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import cm.aptoide.pt.v8engine.analytics.events.FlurryEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pedroribeiro on 26/06/17.
 */

public class StoreAnalytics {

  private static final java.lang.String STORES_TAB_OPEN = "Stores_Tab_Open";
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
}
