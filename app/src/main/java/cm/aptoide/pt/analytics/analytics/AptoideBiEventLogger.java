package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.logger.Logger;
import java.util.Map;

/**
 * Created by trinkes on 10/01/2018.
 */

public class AptoideBiEventLogger implements EventLogger {
  private static final String TAG = AptoideBiEventLogger.class.getSimpleName();
  private final AptoideBiAnalytics service;

  public AptoideBiEventLogger(AptoideBiAnalytics service) {
    this.service = service;
  }

  @Override
  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    service.log(eventName, data, action, context);
    Logger.d(TAG, "log() called with: "
        + "eventName = ["
        + eventName
        + "], data = ["
        + data
        + "], action = ["
        + action
        + "], context = ["
        + context
        + "]");
  }

  @Override public void setup() {
    service.setup();
  }
}
