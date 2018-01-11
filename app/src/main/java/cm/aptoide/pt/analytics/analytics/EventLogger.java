package cm.aptoide.pt.analytics.analytics;

import java.util.Map;

public interface EventLogger {
  void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context);
}
