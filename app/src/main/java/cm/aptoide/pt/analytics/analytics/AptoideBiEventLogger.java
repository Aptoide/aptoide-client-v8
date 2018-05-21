package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.logger.Logger;
import java.util.Map;

/**
 * Created by trinkes on 10/01/2018.
 */

public class AptoideBiEventLogger implements EventLogger, SessionLogger {
  private static final String TAG = AptoideBiEventLogger.class.getSimpleName();
  private final AptoideBiAnalytics service;
  private final long sessionInterval;

  private final String EVENT_NAME = "SESSION";
  private final String CONTEXT_NAME = "APPLICATION";

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
      service.log(EVENT_NAME, null, AnalyticsManager.Action.OPEN, CONTEXT_NAME);
    }
    service.saveTimestamp(System.currentTimeMillis());
  }

  @Override public void endSession() {

  }
}
