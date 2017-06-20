package cm.aptoide.pt.v8engine.analytics.events;

import cm.aptoide.pt.v8engine.analytics.Event;
import com.flurry.android.FlurryAgent;
import java.util.HashMap;

/**
 * Created by pedroribeiro on 19/06/17.
 */

public class FlurryEvent implements Event {

  private final String eventName;
  private HashMap<String, String> map;

  public FlurryEvent(String eventName, HashMap<String, String> map) {
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
