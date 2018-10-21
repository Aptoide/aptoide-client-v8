package cm.aptoide.analytics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsManager {
  private static final String TAG = AnalyticsManager.class.getSimpleName();
  private final KnockEventLogger knockEventLogger;
  private final KeyValueNormalizer analyticsNormalizer;
  private final AnalyticsLogger analyticsLogger;
  private List<SessionLogger> sessionLoggers;
  private Map<EventLogger, Collection<String>> eventLoggers;

  private AnalyticsManager(KnockEventLogger knockLogger,
      Map<EventLogger, Collection<String>> eventLoggers, List<SessionLogger> sessionLoggers,
      KeyValueNormalizer analyticsNormalizer, AnalyticsLogger analyticsLogger) {
    this.knockEventLogger = knockLogger;
    this.eventLoggers = eventLoggers;
    this.sessionLoggers = sessionLoggers;
    this.analyticsNormalizer = analyticsNormalizer;
    this.analyticsLogger = analyticsLogger;
  }

  /**
   * <p>Logs the events to the correspondent event loggers.</p>
   *
   * </p> Only the events whose {@code eventName} is listed in the respective eventLoggers map are
   * logged.</p>
   *
   * @param data The attributes of the event
   * @param eventName The name of the event to be logged.
   * @param action The action done by the user.
   * @param context The context of where the event took place
   */
  public void logEvent(Map<String, Object> data, String eventName, Action action, String context) {
    analyticsLogger.logDebug(TAG, "logEvent() called with: "
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
    data = analyticsNormalizer.normalize(data);
    for (Map.Entry<EventLogger, Collection<String>> loggerEntry : eventLoggers.entrySet()) {
      if (loggerEntry.getValue()
          .contains(eventName)) {
        loggerEntry.getKey()
            .log(eventName, data, action, context);
        eventsSent++;
      }
    }

    if (eventsSent <= 0) {
      analyticsLogger.logWarningDebug(TAG, eventName + " event not sent ");
    }
  }

  /**
   * <p> Makes a simple request with the specified {@code url} with the {@link
   * KnockEventLogger}.</p>
   * <p> Response is not handled.</p>
   *
   * @param url The url to log.
   */
  public void logEvent(String url) {
    knockEventLogger.log(url);
  }

  /**
   * <p>Setup the required {@code EventLogger(s)}, like AptoideBiEventLogger</p>
   */
  public void setup() {
    for (Map.Entry<EventLogger, Collection<String>> loggerEntry : eventLoggers.entrySet()) {
      loggerEntry.getKey()
          .setup();
    }
  }

  /**
   * <p> Starts the Flurry session allowing to log events.</p>
   */
  public void startSession() {
    for (SessionLogger sessionLogger : sessionLoggers) {
      sessionLogger.startSession();
    }
  }

  /**
   * <p> Ends Flurry session. </p>
   */
  public void endSession() {
    for (SessionLogger sessionLogger : sessionLoggers) {
      sessionLogger.endSession();
    }
  }

  /**
   * <p>Possible actions, that were performed by the user, to log</p>
   */
  public enum Action {
    CLICK, SCROLL, INPUT, AUTO, ROOT, VIEW, INSTALL, OPEN, IMPRESSION, PULL_REFRESH, DISMISS, ENDLESS_SCROLL
  }

  /**
   * <p>Builds an AnalyticsManager with a list of EventLoggers, an HttpKnockEventLogger
   * and a SessionLogger.</p>
   */
  public static class Builder {
    private final Map<EventLogger, Collection<String>> eventLoggers;
    private KnockEventLogger knockEventLogger;
    private List<SessionLogger> sessionLoggers;
    private KeyValueNormalizer analyticsNormalizer;
    private AnalyticsLogger analyticsLogger;

    /**
     * <p>Start the builder.</p>
     */
    public Builder() {
      sessionLoggers = new ArrayList<>();
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
      this.sessionLoggers.add(sessionLogger);
      return this;
    }

    /**
     * <p>Sets a logger that logs every event that is sent by the analytics lib.</p>
     *
     * @param logger the logger implementation
     *
     * @return A builder with the added {@link AnalyticsLogger}
     */
    public Builder setDebugLogger(AnalyticsLogger logger) {
      this.analyticsLogger = logger;
      return this;
    }

    /**
     * <p>Sets a {@link KnockEventLogger} that will allow to register a
     * service to log a Knock URL {@code String}.</p>
     *
     * <p>If this builder was not started yet (see {@link #Builder()}), a
     * {@link NullPointerException} will occur.</p>
     *
     * @param knockEventLogger The {@code knockEventLogger} to log the events.
     *
     * @return A builder with the added {@link KnockEventLogger}.
     *
     * @see NullPointerException
     */
    public Builder setKnockLogger(KnockEventLogger knockEventLogger) {
      this.knockEventLogger = knockEventLogger;
      return this;
    }

    /**
     * <p>Sets a {@link KeyValueNormalizer} that will allow to normalize event attributes
     * according to the normalizer implementation.</p>
     *
     * @param analyticsNormalizer The {@code analyticsNormalizer} to normalize the events data.
     *
     * @return A builder with the updated {@link KeyValueNormalizer}
     */
    public Builder setAnalyticsNormalizer(KeyValueNormalizer analyticsNormalizer) {
      this.analyticsNormalizer = analyticsNormalizer;
      return this;
    }

    /**
     * <p>Builds an AnalyticsManager object.</p>
     *
     * <p> An AnalyticsManager needs an {@link KnockEventLogger} and at least one {@link
     * EventLogger}.</p>
     *
     * <p>If this builder was not started ( see {@link #Builder()} ), a
     * {@link NullPointerException} will occur.</p>
     *
     * <p>If no {@link KnockEventLogger} was added (see {@link #setKnockLogger(KnockEventLogger)})
     * an IllegalArgumentException will be thrown.</p>
     *
     * <p>If at least one {@link EventLogger} was not added (see {@link
     * #addLogger(EventLogger, Collection)}, an IllegalArgumentException will be thrown.</p>
     *
     * @return An AnalyticsManager object with a {@code KnockEventLogger}, {@code EventLogger}
     * and {@code SessionLogger}.
     *
     * @see NullPointerException
     */
    public AnalyticsManager build() {
      if (knockEventLogger == null) {
        throw new IllegalArgumentException("Analytics manager need an okhttp client");
      }
      if (eventLoggers.size() < 1) {
        throw new IllegalArgumentException("Analytics manager need at least one logger");
      }
      if (analyticsLogger == null) {
        throw new IllegalArgumentException("Analytics manager need a Debug Logger");
      }
      return new AnalyticsManager(knockEventLogger, eventLoggers, sessionLoggers,
          analyticsNormalizer, analyticsLogger);
    }
  }
}
