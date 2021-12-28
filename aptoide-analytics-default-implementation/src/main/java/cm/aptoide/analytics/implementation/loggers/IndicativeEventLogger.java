package cm.aptoide.analytics.implementation.loggers;

import cm.aptoide.analytics.AnalyticsLogger;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.EventLogger;
import com.indicative.client.android.Indicative;
import java.util.Map;

public class IndicativeEventLogger implements EventLogger {

  private static final String TAG = "IndicativeEventLogger";
  private final AnalyticsLogger logger;

  public IndicativeEventLogger(AnalyticsLogger logger) {
    this.logger = logger;
  }

  @Override
  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    if (data != null) {
      Indicative.recordEvent(eventName, data);
    } else {
      Indicative.recordEvent(eventName);
    }
    logger.logDebug(TAG, "log() called with: "
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
    //started on AptoideApplication
  }
}
