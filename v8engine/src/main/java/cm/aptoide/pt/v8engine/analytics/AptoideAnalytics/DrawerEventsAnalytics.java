package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics;

import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pedroribeiro on 17/04/17.
 */

public class DrawerEventsAnalytics extends AptoideAnalytics implements DrawerAnalytics {

  private final Analytics analytics;
  private final AppEventsLogger facebook;

  public DrawerEventsAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  @Override public void drawerOpen() {
    analytics.sendEvent(new FacebookEvent(facebook, "Drawer_Opened"));
  }

  @Override public void drawerInteract(String origin) {
    analytics.sendEvent(
        new FacebookEvent(facebook, "Drawer_Interact", createBundleData("action", origin)));
  }
}
