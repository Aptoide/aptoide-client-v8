package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.Event;
import com.localytics.android.Localytics;
import java.util.Map;

/**
 * Created by marcelobenites on 03/03/17.
 */

public class LocalyticsEvent implements Event {

  private final String name;

  private Map<String, String> data;

  public LocalyticsEvent(String name) {
    this.name = name;
  }

  public LocalyticsEvent(String name, Map<String, String> data) {
    this.name = name;
    this.data = data;
  }

  @Override public void send() {
    // TODO Refactor initialization logic out of Analytics class and reuse it here.
    try {
      if (data != null) {
        Localytics.tagEvent(name, data);
      } else {
        Localytics.tagEvent(name);
      }
    } catch (Exception e) {
    }
  }
}