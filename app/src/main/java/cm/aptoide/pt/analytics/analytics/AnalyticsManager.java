package cm.aptoide.pt.analytics.analytics;

import android.support.annotation.NonNull;
import cm.aptoide.pt.analytics.AnalyticsDataSaver;
import cm.aptoide.pt.logger.Logger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsManager {
  public static final String FIRST_INSTALL_POP_UP = "First_Install_Pop_up";
  public static final String FIRST_INSTALL_CLOSE_WINDOW = "First_Install_Close_Window";
  public static final String FIRST_INSTALL_START_DOWNLOAD = "First_Install_Start_Download";
  private static final String TAG = AnalyticsManager.class.getSimpleName();
  private final HttpKnockEventLogger knockEventLogger;

  private Map<EventLogger, Collection<String>> eventSenders;
  private AnalyticsDataSaver analyticsDataSaver;

  private AnalyticsManager(HttpKnockEventLogger knockLogger,
      Map<EventLogger, Collection<String>> eventSenders, AnalyticsDataSaver analyticsDataSaver) {
    this.knockEventLogger = knockLogger;
    this.eventSenders = eventSenders;
    this.analyticsDataSaver = analyticsDataSaver;
  }

  public void logEvent(Map<String, Object> data, String eventName, Action action, String context) {
    Logger.d(TAG, "logEvent() called with: "
        + "data = ["
        + data
        + "], eventName = ["
        + eventName
        + "], action = ["
        + action
        + "], context = ["
        + context
        + "]");
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

  public void logEvent(Event event) {
    logEvent(event.getData(), event.getEventName(), event.getAction(), event.getContext());
  }

  public void logEvent(@NonNull String url) {
    knockEventLogger.log(url);
  }

  public void setup() {
    for (Map.Entry<EventLogger, Collection<String>> senderEntry : eventSenders.entrySet()) {
      senderEntry.getKey()
          .setup();
    }
  }

  public void save(@NonNull String key, @NonNull Event event) {
    analyticsDataSaver.save(key, event);
  }

  public Event getEvent(String key) {
    return analyticsDataSaver.newGet(key);
  }

  public enum Action {
    CLICK, SCROLL, INPUT, AUTO, ROOT, VIEW, INSTALL, OPEN, IMPRESSION, DISMISS
  }

  public static class Builder {
    private final Map<EventLogger, Collection<String>> eventSenders;
    private HttpKnockEventLogger httpKnockEventLogger;
    private AnalyticsDataSaver analyticsDataSaver;

    public Builder() {
      eventSenders = new HashMap<>();
    }

    public Builder addLogger(EventLogger eventLogger, Collection<String> supportedEvents) {
      eventSenders.put(eventLogger, supportedEvents);
      return this;
    }

    public Builder setKnockLogger(HttpKnockEventLogger httpKnockEventLogger) {
      this.httpKnockEventLogger = httpKnockEventLogger;
      return this;
    }

    public Builder addDataSaver(AnalyticsDataSaver analyticsDataSaver) {
      this.analyticsDataSaver = analyticsDataSaver;
      return this;
    }

    public AnalyticsManager build() {
      if (httpKnockEventLogger == null) {
        throw new IllegalArgumentException("Analytics manager need an okhttp client");
      }
      if (eventSenders.size() < 1) {
        throw new IllegalArgumentException("Analytics manager need at least one logger");
      }
      return new AnalyticsManager(httpKnockEventLogger, eventSenders, analyticsDataSaver);
    }
  }
}
