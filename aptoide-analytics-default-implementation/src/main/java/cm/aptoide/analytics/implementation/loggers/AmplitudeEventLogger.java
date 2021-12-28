package cm.aptoide.analytics.implementation.loggers;

import cm.aptoide.analytics.AnalyticsLogger;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.EventLogger;
import cm.aptoide.analytics.implementation.utils.MapToJsonMapper;
import com.amplitude.api.Amplitude;
import java.util.Map;

public class AmplitudeEventLogger implements EventLogger {

  private static final String TAG = "AmplitudeEventLogger";
  private final AnalyticsLogger logger;
  private final MapToJsonMapper jsonMapper;

  public AmplitudeEventLogger(AnalyticsLogger analyticsLogger, MapToJsonMapper jsonMapper) {
    this.logger = analyticsLogger;
    this.jsonMapper = jsonMapper;
  }

  @Override
  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    if (data != null) {
      Amplitude.getInstance()
          .logEvent(eventName, jsonMapper.mapToJsonObject(data));
    } else {
      Amplitude.getInstance()
          .logEvent(eventName);
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
    //Started on Aptoide Application
  }
}
