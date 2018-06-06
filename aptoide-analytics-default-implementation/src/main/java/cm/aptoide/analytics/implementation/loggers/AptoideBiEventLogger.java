package cm.aptoide.analytics.implementation.loggers;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.EventLogger;
import cm.aptoide.analytics.SessionLogger;
import cm.aptoide.analytics.implementation.AptoideBiAnalytics;
import java.util.Map;

/**
 * Created by trinkes on 10/01/2018.
 */

public class AptoideBiEventLogger implements EventLogger, SessionLogger {
  private final AptoideBiAnalytics service;
  private final long sessionInterval;

  public AptoideBiEventLogger(AptoideBiAnalytics service, long sessionInterval) {
    this.service = service;
    this.sessionInterval = sessionInterval;
  }

  @Override
  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    service.log(eventName, data, action, context);
  }

  @Override public void setup() {
    service.setup();
  }

  @Override public void startSession() {
    long currentTimeElapsed = System.currentTimeMillis() - service.getTimestamp();
    if (currentTimeElapsed > sessionInterval) {
      service.log("SESSION", null, AnalyticsManager.Action.OPEN, "APPLICATION");
    }
    service.saveTimestamp(System.currentTimeMillis());
  }

  @Override public void endSession() {
  }
}
