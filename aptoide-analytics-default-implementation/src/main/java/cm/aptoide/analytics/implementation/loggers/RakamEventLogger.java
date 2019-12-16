package cm.aptoide.analytics.implementation.loggers;

import cm.aptoide.analytics.AnalyticsLogger;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.EventLogger;
import cm.aptoide.analytics.SessionLogger;
import io.rakam.api.Rakam;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class RakamEventLogger implements EventLogger, SessionLogger {

  private static final String TAG = "RakamEventLogger";
  private final AnalyticsLogger logger;

  public RakamEventLogger(AnalyticsLogger logger) {
    this.logger = logger;
  }

  @Override
  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    if (data != null) {
      Rakam.getInstance()
          .logEvent(eventName, mapToJsonObject(data));
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

  private JSONObject mapToJsonObject(Map<String, Object> data) {
    JSONObject eventData = new JSONObject();
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      if (entry.getValue() != null) {
        try {
          eventData.put(entry.getKey(), entry.getValue()
              .toString());
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
    return eventData;
  }

  @Override public void startSession() {
    //According to rakam documentation: Sessions are handled automatically now; you no longer have to manually call startSession() or endSession().
  }

  @Override public void endSession() {
    //According to rakam documentation: Sessions are handled automatically now; you no longer have to manually call startSession() or endSession().
  }
}
