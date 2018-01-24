package cm.aptoide.pt;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by franciscocalado on 1/24/18.
 */

public class AppShortcutsAnalytics {

  private static final String DESTINATION = "destination";
  private static final String APPS_SHORTCUTS = "apps_shortcuts";

  private final AppEventsLogger facebook;
  private final Analytics analytics;

  public AppShortcutsAnalytics(AppEventsLogger facebook, Analytics analytics) {
    this.facebook = facebook;
    this.analytics = analytics;
  }

  public void shortcutNavigation(String destination) {
    Bundle bundle = new Bundle();
    bundle.putString(DESTINATION, destination);

    analytics.sendEvent(new FacebookEvent(facebook, APPS_SHORTCUTS, bundle));
  }
}
