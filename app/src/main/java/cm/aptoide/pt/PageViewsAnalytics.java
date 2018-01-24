package cm.aptoide.pt;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

public class PageViewsAnalytics {

  private final AppEventsLogger facebook;
  private final Analytics analytics;

  public PageViewsAnalytics(AppEventsLogger facebook, Analytics analytics) {
    this.facebook = facebook;
    this.analytics = analytics;
  }

  public void sendPageViewedEvent(String currentViewName) {
    analytics.sendEvent(new FacebookEvent(facebook, "Page_View",
        createEventBundle(currentViewName)));
  }

  private Bundle createEventBundle(String currentViewName) {
    Bundle bundle = new Bundle();
    bundle.putString("fragment", currentViewName);
    return bundle;
  }
}
