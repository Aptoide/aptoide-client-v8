package cm.aptoide.analytics;

import java.util.Map;

public interface DebugLogger {

  void logDebug(String tag, Map<String, Object> data, String eventName,
      AnalyticsManager.Action action, String context);

  void logEventNotSent(String tag, String eventName);
}
