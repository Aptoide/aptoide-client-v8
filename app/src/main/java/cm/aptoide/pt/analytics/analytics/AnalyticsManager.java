package cm.aptoide.pt.analytics.analytics;

import android.support.annotation.NonNull;
import cm.aptoide.pt.ApplicationModule;
import cm.aptoide.pt.FlavourApplicationModule;
import cm.aptoide.pt.logger.Logger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsManager {
  private static final String TAG = AnalyticsManager.class.getSimpleName();
  private final HttpKnockEventLogger knockEventLogger;
  private final SessionLogger sessionLogger;

  private Map<EventLogger, Collection<String>> eventLoggers;

  private AnalyticsManager(HttpKnockEventLogger knockLogger,
      Map<EventLogger, Collection<String>> eventLoggers, SessionLogger sessionLogger) {
    this.knockEventLogger = knockLogger;
    this.eventLoggers = eventLoggers;
    this.sessionLogger = sessionLogger;
  }

  /**
   * Logs the events to the correspondent event loggers.
   *
   * Only the events whose {@code eventName} is listed on {@link FlavourApplicationModule} or {@link
   * ApplicationModule
   * }
   *
   * @param data The attributes of the event
   * @param eventName The name of the event to be logged.
   * @param action The action done by the user.
   * @param context The context of where the event took place
   */
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

  /**
   * <p> Log a {@code url} to the {@link HttpKnockEventLogger}.</p>
   *
   * @param url The url to log.
   */
  public void logEvent(@NonNull String url) {
    knockEventLogger.log(url);
  }

  /**
   * <p>Setup the Aptoide Bi analytics.</p>
   */
  public void setup() {
    for (Map.Entry<EventLogger, Collection<String>> loggerEntry : eventLoggers.entrySet()) {
      loggerEntry.getKey()
          .setup();
    }
  }

  /**
   * <p> Starts the Flurry session allowing to log events to Flurry.</p>
   */
  public void startSession() {
    sessionLogger.startSession();
  }

  /**
   * <p> Ends Flurry session. </p>
   */
  public void endSession() {
    sessionLogger.endSession();
  }

  /**
   * <p>This represents the different possible actions performed by the user.</p>
   */
  public enum Action {
    CLICK, SCROLL, INPUT, AUTO, ROOT, VIEW, INSTALL, OPEN, IMPRESSION, DISMISS
  }

  /**
   * <p>Builds an AnalyticsManager with a list of EventLoggers, an HttpKnockEventLogger
   * and a SessionLogger.</p>
   */
  public static class Builder {
    private final Map<EventLogger, Collection<String>> eventLoggers;
    private HttpKnockEventLogger httpKnockEventLogger;
    private SessionLogger sessionLogger;

    /**
     * <p>Start the builder.</p>
     */
    public Builder() {
      eventLoggers = new HashMap<>();
    }

    /**
     * <p>Adds an {@link EventLogger} and the respective {@code supportedEvents }. </p>
     * <p>This {@code eventLogger} will allow to register a service to log any of the {@code
     * supportedEvents
     * }.</p>
     *
     * <p>If this builder was not started yet (see {@link #Builder()}), a
     * {@link NullPointerException} will occur.</p>
     *
     * @param eventLogger The EventLogger to add.
     * @param supportedEvents A collection of the possible events associated with the {@code
     * eventLogger}.
     *
     * @return A builder with the added {@code eventLogger} and respective {@code supportedEvents}.
     *
     * @see NullPointerException
     */
    public Builder addLogger(EventLogger eventLogger, Collection<String> supportedEvents) {
      eventLoggers.put(eventLogger, supportedEvents);
      return this;
    }

    /**
     * <p>Adds a {@link SessionLogger}.</p>
     * <p>This {@code sessionLogger} will allow to start and stop a Flurry session.</p>
     *
     * <p>If this builder was not started yet (see {@link #Builder()}), a
     * {@link NullPointerException} will occur.</p>
     *
     * @param sessionLogger A flurry SessionLogger.
     *
     * @return A builder with the added {@code sessionLogger}.
     *
     * @see NullPointerException
     */
    public Builder addSessionLogger(SessionLogger sessionLogger) {
      this.sessionLogger = sessionLogger;
      return this;
    }

    /**
     * <p>Sets a {@link HttpKnockEventLogger} that will allow to register a
     * service to log a Knock URL {@code String}.</p>
     *
     * <p>If this builder was not started yet (see {@link #Builder()}), a
     * {@link NullPointerException} will occur.</p>
     *
     * @param httpKnockEventLogger The {@code httpKnockEventLogger} to log the events.
     *
     * @return A builder with the added {@link HttpKnockEventLogger}.
     *
     * @see NullPointerException
     */
    public Builder setKnockLogger(HttpKnockEventLogger httpKnockEventLogger) {
      this.httpKnockEventLogger = httpKnockEventLogger;
      return this;
    }

    /**
     * <p>Creates an AnalyticsManager.</p>
     *
     * <p>If this builder was not started ( see {@link #Builder()} ), a
     * {@link NullPointerException} will occur.</p>
     *
     * <p>An AnalyticsManager needs a {@link HttpKnockEventLogger}, so if no {@link
     * HttpKnockEventLogger} was added (see {@link
     * #setKnockLogger(HttpKnockEventLogger)} ) an IllegalArgumentException will be thrown.</p>
     *
     * <p>An AnalyticsManager needs at least one {@link EventLogger}, so if no {@link EventLogger}
     * were added (see {@link #addLogger(EventLogger, Collection)},
     * an IllegalArgumentException will be thrown.</p>
     *
     * @return An AnalyticsManager object with a {@code HttpKnockEventLogger}, {@code EventLogger}
     * and {@code SessionLogger}.
     *
     * @see NullPointerException
     */
    public AnalyticsManager build() {
      if (httpKnockEventLogger == null) {
        throw new IllegalArgumentException("Analytics manager need an okhttp client");
      }
      if (eventLoggers.size() < 1) {
        throw new IllegalArgumentException("Analytics manager need at least one logger");
      }
      return new AnalyticsManager(httpKnockEventLogger, eventLoggers, sessionLogger);
    }
  }
}
