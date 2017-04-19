package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics;

import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pedroribeiro on 18/04/17.
 */

public class UpdatesEventsAnalytics extends AptoideAnalytics implements UpdatesAnalytics {

  private Analytics analytics;
  private AppEventsLogger facebook;

  public UpdatesEventsAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  @Override public void updates(String action) {
    analytics.sendEvent(new FacebookEvent(facebook, "Updates", createBundleData("action", action)));
  }
}
