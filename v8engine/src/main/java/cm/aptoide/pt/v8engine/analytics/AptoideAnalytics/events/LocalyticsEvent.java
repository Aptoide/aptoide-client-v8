package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.Event;
import com.facebook.appevents.AppEventsLogger;
import com.localytics.android.Localytics;

/**
 * Created by marcelobenites on 03/03/17.
 */

public class LocalyticsEvent implements Event {

  private final String name;

  public LocalyticsEvent(String name) {
    this.name = name;
  }

  @Override public void send() {
    // TODO Refactor initialization logic out of Analytics class and reuse it here.
    try {
      Localytics.tagEvent(name);
    } catch (Exception e) {}
  }
}