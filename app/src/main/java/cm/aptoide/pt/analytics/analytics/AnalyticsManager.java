package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.logger.Logger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsManager {
  private static final String TAG = AnalyticsManager.class.getSimpleName();
  private Map<EventLogger, Collection<String>> eventSenders;

  private AnalyticsManager(Map<EventLogger, Collection<String>> eventSenders) {
    this.eventSenders = eventSenders;
  }

  public void logEvent(Map<String, Object> data, String eventName, Action action, String context) {
    int eventsSent = 0;
    for (Map.Entry<EventLogger, Collection<String>> senderEntry : eventSenders.entrySet()) {
      if (senderEntry.getValue()
          .contains(eventName)) {
        senderEntry.getKey()
            .log(eventName, data, action, context);
        eventsSent++;
      }
    }

    if (eventsSent <= 0) {
      Logger.w(TAG, eventName + " event not sent ");
    }
  }

  public void setup() {
    for (Map.Entry<EventLogger, Collection<String>> senderEntry : eventSenders.entrySet()) {
      senderEntry.getKey()
          .setup();
    }
  }

  public enum Action {
    CLICK, SCROLL, INPUT, AUTO, ROOT, VIEW, INSTALL, OPEN, IMPRESSION, DISMISS
  }

  public static class Builder {
    private final Map<EventLogger, Collection<String>> eventSenders;

    public Builder() {
      eventSenders = new HashMap<>();
    }

    public Builder addLogger(EventLogger eventLogger, Collection<String> supportedEvents) {
      eventSenders.put(eventLogger, supportedEvents);
      return this;
    }

    public AnalyticsManager build() {
      return new AnalyticsManager(eventSenders);
    }
  }
}
