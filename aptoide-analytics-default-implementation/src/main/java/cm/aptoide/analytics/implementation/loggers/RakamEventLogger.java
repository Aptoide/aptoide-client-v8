package cm.aptoide.analytics.implementation.loggers;

import cm.aptoide.analytics.AnalyticsLogger;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.EventLogger;
import cm.aptoide.analytics.implementation.utils.MapToJsonMapper;
import io.rakam.api.Rakam;
import java.util.Map;

public class RakamEventLogger implements EventLogger {

  private static final String TAG = "RakamEventLogger";
  private final AnalyticsLogger logger;
  private final MapToJsonMapper jsonMapper;

  public RakamEventLogger(AnalyticsLogger logger, MapToJsonMapper jsonMapper) {
    this.logger = logger;
    this.jsonMapper = jsonMapper;
  }

  @Override
  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    if (data != null) {
      Rakam.getInstance()
          .logEvent(eventName, jsonMapper.mapToJsonObject(data));
    } else {
      Rakam.getInstance()
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
    //started on AptoideApplication
  }
}
