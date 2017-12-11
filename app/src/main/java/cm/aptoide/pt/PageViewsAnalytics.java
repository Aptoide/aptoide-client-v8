package cm.aptoide.pt;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

public class PageViewsAnalytics {

  private final AppEventsLogger facebook;
  private final Analytics analytics;
  private final NavigationTracker navigationTracker;

  public PageViewsAnalytics(AppEventsLogger facebook, Analytics analytics,
      NavigationTracker navigationTracker) {
    this.facebook = facebook;
    this.analytics = analytics;
    this.navigationTracker = navigationTracker;
  }

  public void sendPageViewedEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Page_View",
        createEventBundle(navigationTracker.getCurrentViewName())));
  }

  private Bundle createEventBundle(String currentViewName) {
    Bundle bundle = new Bundle();
    bundle.putString("fragment", currentViewName);
    return bundle;
  }
}
