package cm.aptoide.pt;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pedroribeiro on 26/09/17.
 */

public class PageViewsAnalytics {

  private final AppEventsLogger facebook;
  private final Analytics analytics;
  private AptoideNavigationTracker aptoideNavigationTracker;

  public PageViewsAnalytics(AppEventsLogger facebook, Analytics analytics,
      AptoideNavigationTracker aptoideNavigationTracker) {
    this.facebook = facebook;
    this.analytics = analytics;
    this.aptoideNavigationTracker = aptoideNavigationTracker;
  }

  public void sendPageViewedEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, "Page_View",
        createEventBundle(aptoideNavigationTracker.getCurrentViewName())));
  }

  private Bundle createEventBundle(String currentViewName) {
    Bundle bundle = new Bundle();
    bundle.putString("fragment", currentViewName);
    return bundle;
  }
}
