package cm.aptoide.analytics.implementation.loggers;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.DebugLogger;
import cm.aptoide.analytics.EventLogger;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import java.util.Map;
import java.util.Set;

public class FabricEventLogger implements EventLogger {
  private static final String TAG = FabricEventLogger.class.getSimpleName();
  private final Answers fabric;
  private final DebugLogger logger;

  public FabricEventLogger(Answers fabric, DebugLogger logger) {
    this.fabric = fabric;
    this.logger = logger;
  }

  @Override
  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    CustomEvent customEvent = new CustomEvent(eventName);
    if (data != null && !data.isEmpty()) {
      Set<Map.Entry<String, Object>> dataEntry = data.entrySet();

      for (Map.Entry<String, Object> attribute : dataEntry) {
        customEvent.putCustomAttribute(attribute.getKey(), attribute.getValue()
            .toString());
      }
    }
    logger.d(TAG, "log() called with: "
        + "eventName = ["
        + eventName
        + "], data = ["
        + data
        + "], action = ["
        + action
        + "], context = ["
        + context
        + "]");
    fabric.logCustom(customEvent);
  }

  @Override public void setup() {

  }
}
