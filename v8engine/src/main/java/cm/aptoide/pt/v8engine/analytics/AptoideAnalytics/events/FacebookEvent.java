package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.Event;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by marcelobenites on 03/03/17.
 */

public class FacebookEvent implements Event {

  private final AppEventsLogger facebook;
  private final String name;
  private Bundle data;

  public FacebookEvent(AppEventsLogger facebook, String name) {
    this.facebook = facebook;
    this.name = name;
  }

  @Override public void send() {
    facebook.logEvent(name);
  }
}
