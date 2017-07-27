package cm.aptoide.pt.v8engine.timeline.post;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by trinkes on 27/07/2017.
 */

public class PostAnalytics {
  public static final String OPEN_EVENT_NAME = "New_Post_Open";
  public static final String NEW_POST_EVENT_NAME = "New_Post_Close";
  private final Analytics analytics;
  private final AppEventsLogger facebook;

  public PostAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void sendOpenEvent(OpenSource source) {
    Bundle bundle = new Bundle();
    bundle.putSerializable("source", source);
    analytics.sendEvent(new FacebookEvent(facebook, OPEN_EVENT_NAME, bundle));
  }

  public void sendClosePostEvent(CloseType closeType) {
    Bundle bundle = new Bundle();
    bundle.putSerializable("New_Post_Close", closeType);
    analytics.sendEvent(new FacebookEvent(facebook, NEW_POST_EVENT_NAME, bundle));
  }

  enum OpenSource {
    APP_TIMELINE, EXTERNAL
  }

  enum CloseType {
    X, BACK
  }
}
