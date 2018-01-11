package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.logger.Logger;
import java.util.Map;

public class AptoideBiAnalytics {
  private static final String TAG = AptoideBiAnalytics.class.getSimpleName();
  EventsPersistence persistence;

  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
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
}
