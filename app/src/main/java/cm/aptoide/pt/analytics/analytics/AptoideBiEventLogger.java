package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.logger.Logger;
import java.util.Map;

/**
 * Created by trinkes on 10/01/2018.
 */

public class AptoideBiEventLogger implements EventLogger, SessionLogger {
  private static final String TAG = AptoideBiEventLogger.class.getSimpleName();
  private final AptoideBiAnalytics service;

  private final String EVENT_NAME = "SESSION";
  private final String CONTEXT_NAME = "APPLICATION";

  public AptoideBiEventLogger(AptoideBiAnalytics service) {
    this.service = service;
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
    //service.getTimestamp(); //todo
    long thirtySeconds = 30 * 1000;
    long currentTimeElapsed = System.currentTimeMillis() - service.getTimestamp();
    if (currentTimeElapsed < thirtySeconds) {
      Logger.d(TAG, "startSession currentTimeElapsed: " + currentTimeElapsed);
    } else {
      Logger.d(TAG, "startSession: " + service.getTimestamp());
      service.log(EVENT_NAME, null, AnalyticsManager.Action.OPEN, CONTEXT_NAME,
          System.currentTimeMillis());
    }
  }

  @Override public void endSession() {

  }
}
