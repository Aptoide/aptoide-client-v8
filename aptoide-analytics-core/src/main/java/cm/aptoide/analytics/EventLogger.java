package cm.aptoide.analytics;

import java.util.Map;

public interface EventLogger {
  /**
   * <p>Sends an event with parameters.</p>
   *
   * @param eventName The name of the event to be logged.
   * @param data The attributes of the event.
   * @param action The action done by the user.
   * @param context The context of where the event took place.
   */
  void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context);

  /**
   * <p>Initializes the environment to allow logging this EventLogger's associated events.</p>
   */
  void setup();
}
