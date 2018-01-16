package cm.aptoide.pt.analytics.analytics;

import java.util.Map;

/**
 * Created by trinkes on 10/01/2018.
 */

public class Event {
  private final String eventName;

  private final Map<String, Object> data;
  private final AnalyticsManager.Action action;
  private final String context;
  private final long timeStamp;

  public Event(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context, long timeStamp) {
    this.eventName = eventName;
    this.data = data;
    this.action = action;
    this.context = context;
    this.timeStamp = timeStamp;
  }

  @Override public int hashCode() {
    int result = eventName.hashCode();
    result = 31 * result + (data != null ? data.hashCode() : 0);
    result = 31 * result + action.hashCode();
    result = 31 * result + context.hashCode();
    result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Event)) return false;

    Event event = (Event) o;

    if (timeStamp != event.timeStamp) return false;
    if (!eventName.equals(event.eventName)) return false;
    if (data != null ? !data.equals(event.data) : event.data != null) return false;
    if (action != event.action) return false;
    return context.equals(event.context);
  }

  public String getEventName() {
    return eventName;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public AnalyticsManager.Action getAction() {
    return action;
  }

  public String getContext() {
    return context;
  }

  public long getTimeStamp() {
    return timeStamp;
  }
}
