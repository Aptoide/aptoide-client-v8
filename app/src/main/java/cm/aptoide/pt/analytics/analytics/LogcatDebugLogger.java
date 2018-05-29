package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.logger.Logger;
import java.util.Map;

public class LogcatDebugLogger implements DebugLogger {
  @Override public void logDebug(String tag, Map<String, Object> data, String eventName,
      AnalyticsManager.Action action, String context) {
    Logger.d(tag, "logEvent() called with: "
        + "data = ["
        + data
        + "], eventName = ["
        + eventName
        + "], action = ["
        + action
        + "], context = ["
        + context
        + "]");
  }
}
