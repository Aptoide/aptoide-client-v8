package cm.aptoide.pt.analytics.analytics;

import android.support.annotation.NonNull;
import cm.aptoide.pt.logger.Logger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsManager {
  private static final String TAG = AnalyticsManager.class.getSimpleName();
  private final HttpKnockEventLogger knockEventLogger;

  private Map<EventLogger, Collection<String>> eventLoggers;

  private AnalyticsManager(HttpKnockEventLogger knockLogger,
      Map<EventLogger, Collection<String>> eventLoggers) {
    this.knockEventLogger = knockLogger;
    this.eventLoggers = eventLoggers;
  }

  public void logEvent(Map<String, Object> data, String eventName, Action action, String context) {
    int eventsSent = 0;
    for (Map.Entry<EventLogger, Collection<String>> loggerEntry : eventLoggers.entrySet()) {
      if (loggerEntry.getValue()
          .contains(eventName)) {
        loggerEntry.getKey()
            .log(eventName, data, action, context);
        eventsSent++;
      }
    }

    if (eventsSent <= 0) {
      Logger.w(TAG, eventName + " event not sent ");
    }
  }

  public void logEvent(@NonNull String url) {
    knockEventLogger.log(url);
  }

  public void setup() {
    for (Map.Entry<EventLogger, Collection<String>> loggerEntry : eventLoggers.entrySet()) {
      loggerEntry.getKey()
          .setup();
    }
  }

  public enum Action {
    CLICK, SCROLL, INPUT, AUTO, ROOT, VIEW, INSTALL, OPEN, IMPRESSION, DISMISS
  }

  public static class Builder {
    private final Map<EventLogger, Collection<String>> eventLoggers;
    private HttpKnockEventLogger httpKnockEventLogger;

    public Builder() {
      eventLoggers = new HashMap<>();
    }

    public Builder addLogger(EventLogger eventLogger, Collection<String> supportedEvents) {
      eventLoggers.put(eventLogger, supportedEvents);
      return this;
    }

    public Builder setKnockLogger(HttpKnockEventLogger httpKnockEventLogger) {
      this.httpKnockEventLogger = httpKnockEventLogger;
      return this;
    }

    public AnalyticsManager build() {
      if (httpKnockEventLogger == null) {
        throw new IllegalArgumentException("Analytics manager need an okhttp client");
      }
      if (eventLoggers.size() < 1) {
        throw new IllegalArgumentException("Analytics manager need at least one logger");
      }
      return new AnalyticsManager(httpKnockEventLogger, eventLoggers);
    }
  }
}
