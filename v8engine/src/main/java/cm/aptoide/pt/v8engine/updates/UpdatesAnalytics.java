package cm.aptoide.pt.v8engine.updates;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pedroribeiro on 18/04/17.
 */

public class UpdatesAnalytics {

  private Analytics analytics;
  private AppEventsLogger facebook;

  public UpdatesAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void updates(String action) {
    analytics.sendEvent(new FacebookEvent(facebook, "Updates", createBundleData("action", action)));
  }

  private Bundle createBundleData(String key, String value) {
    final Bundle data = new Bundle();
    data.putString(key, value);
    return data;
  }
}
