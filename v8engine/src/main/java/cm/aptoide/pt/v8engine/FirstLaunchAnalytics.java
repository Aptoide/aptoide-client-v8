package cm.aptoide.pt.v8engine;

import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import cm.aptoide.pt.v8engine.analytics.events.FlurryEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pedroribeiro on 27/06/17.
 */

public class FirstLaunchAnalytics {

  private static final String FIRST_LAUNCH = "Aptoide_First_Launch";
  private final AppEventsLogger facebook;
  private final Analytics analytics;

  public FirstLaunchAnalytics(AppEventsLogger facebook, Analytics analytics) {
    this.facebook = facebook;
    this.analytics = analytics;
  }

  public void sendFirstLaunchEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, FIRST_LAUNCH));
    analytics.sendEvent(new FlurryEvent(FIRST_LAUNCH));
  }
}
