package cm.aptoide.pt.v8engine.app;

import android.os.Bundle;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pedroribeiro on 10/05/17.
 */

// Open App View

public class AppViewSimilarAppAnalytics {

  private static final String TAG = AppViewSimilarAppAnalytics.class.getSimpleName();

  private static final String ACTION = "Action";
  private Analytics analytics;
  private AppEventsLogger facebook;

  public AppViewSimilarAppAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void similarAppsIsShown() {
    String eventName = EventNames.APP_VIEW_SIMILAR_APP_SLIDE_IN;

    analytics.sendEvent(new FacebookEvent(facebook, eventName));
    Logger.w(TAG, "Facebook Event: " + eventName);
  }

  public void openSimilarApp() {
    String eventName = EventNames.SIMILAR_APP_INTERACT;
    Bundle parameters = createBundleData(ACTION, "Open App View");

    analytics.sendEvent(new FacebookEvent(facebook, eventName, parameters));
    Logger.w(TAG, "Facebook Event: " + eventName + " : " + parameters.toString());
  }

  private Bundle createBundleData(String key, String value) {
    final Bundle data = new Bundle();
    data.putString(key, value);
    return data;
  }

  private static final class EventNames {
    private static final String APP_VIEW_SIMILAR_APP_SLIDE_IN = "App_View_Similar_App_Slide_In";
    private static final String SIMILAR_APP_INTERACT = "Similar_App_Interact";
  }
}
