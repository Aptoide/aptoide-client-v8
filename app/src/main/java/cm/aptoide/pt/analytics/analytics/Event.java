package cm.aptoide.pt.analytics.analytics;

import java.util.Map;

/**
 * Created by trinkes on 10/01/2018.
 */

class Event {
  private final String eventName;
  private final Map<String, Object> data;
  private final String action;
  private final String context;

  public Event(String eventName, Map<String, Object> data, String action, String context) {
    this.eventName = eventName;
    this.data = data;
    this.action = action;
    this.context = context;
  }

  public String getEventName() {
    return eventName;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public String getAction() {
    return action;
  }

  public String getContext() {
    return context;
  }
}
