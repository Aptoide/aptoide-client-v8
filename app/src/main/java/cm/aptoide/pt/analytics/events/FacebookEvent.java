package cm.aptoide.pt.analytics.events;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Event;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by marcelobenites on 03/03/17.
 */

public class FacebookEvent implements Event {

  private final AppEventsLogger facebook;
  private final String name;
  private Bundle data;

  public FacebookEvent(AppEventsLogger facebook, String name, Bundle data) {
    this.facebook = facebook;
    this.name = name;
    this.data = data;
  }

  @Override public void send() {
    if (data != null) {
      facebook.logEvent(name, data);
    } else {
      facebook.logEvent(name);
    }
  }
}
