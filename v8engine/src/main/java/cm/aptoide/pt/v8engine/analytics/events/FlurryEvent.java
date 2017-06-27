package cm.aptoide.pt.v8engine.analytics.events;

import cm.aptoide.pt.v8engine.analytics.Event;
import com.flurry.android.FlurryAgent;
import java.util.Map;

public class FlurryEvent implements Event {

  private final String eventName;
  private final Map<String, String> map;

  public FlurryEvent(String eventName, Map<String, String> map) {
    this.eventName = eventName;
    this.map = map;
  }

  @Override public void send() {
    if (map != null) {
      FlurryAgent.logEvent(eventName, map);
    } else {
      FlurryAgent.logEvent(eventName);
    }
  }
}
