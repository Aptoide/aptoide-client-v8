package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.ApplicationModule;
import cm.aptoide.pt.FlavourApplicationModule;
import java.util.Map;

public interface EventLogger {
  /**
   * <p>Sends an {@code event} to the correspondent {@code EventLogger(s)}.</p>
   *
   * <p>Only the events whose {@code eventName} is listed on {@link FlavourApplicationModule} or
   * {@link ApplicationModule} are logged.</p>
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
