package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.ApplicationModule;
import cm.aptoide.pt.FlavourApplicationModule;
import java.util.Map;

public interface EventLogger {
  /**
   * <p>Sends an {@code event} to the {@code EventLogger(s)} where that {@code event} is listed
   * on.</p>
   *
   * Only the events whose {@code eventName} is listed on {@link FlavourApplicationModule} or {@link
   * ApplicationModule
   * } are logged.
   *
   * @param eventName The name of the event to be logged.
   * @param data The attributes of the event.
   * @param action The action done by the user.
   * @param context The context of where the event took place.
   */
  void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action, String context);

  /**
   * <p>Initializes the environment to log certain events.</p>
   */
  void setup();
}
