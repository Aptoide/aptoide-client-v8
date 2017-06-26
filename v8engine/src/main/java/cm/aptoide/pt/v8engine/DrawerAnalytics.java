package cm.aptoide.pt.v8engine;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pedroribeiro on 17/04/17.
 */

public class DrawerAnalytics {

  private final Analytics analytics;
  private final AppEventsLogger facebook;

  public DrawerAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void drawerOpen() {
    analytics.sendEvent(new FacebookEvent(facebook, "Drawer_Opened"));
  }

  public void drawerInteract(String origin) {
    analytics.sendEvent(
        new FacebookEvent(facebook, "Drawer_Interact", createBundleData("action", origin)));
  }

  private Bundle createBundleData(String key, String value) {
    final Bundle data = new Bundle();
    data.putString(key, value);
    return data;
  }
}
