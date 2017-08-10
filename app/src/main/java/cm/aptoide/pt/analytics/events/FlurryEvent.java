package cm.aptoide.pt.analytics.events;

import cm.aptoide.pt.analytics.Event;
import com.flurry.android.FlurryAgent;
import java.util.Map;

public class FlurryEvent implements Event {

  private final String eventName;
  private Map<String, String> map;

  public FlurryEvent(String eventName, Map<String, String> map) {
    this.eventName = eventName;
    this.map = map;
  }

  public FlurryEvent(String eventName) {
    this.eventName = eventName;
  }

  @Override public void send() {
    if (map != null) {
      FlurryAgent.logEvent(eventName, map);
    } else {
      FlurryAgent.logEvent(eventName);
    }
  }
}
