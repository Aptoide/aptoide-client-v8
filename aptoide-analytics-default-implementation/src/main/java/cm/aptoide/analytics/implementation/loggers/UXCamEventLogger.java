package cm.aptoide.analytics.implementation.loggers;

import cm.aptoide.analytics.AnalyticsLogger;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.EventLogger;
import com.uxcam.UXCam;
import java.util.Map;

public class UXCamEventLogger implements EventLogger {

  private static final String TAG = "UXCamEventLogger";
  private final AnalyticsLogger logger;

  public UXCamEventLogger(AnalyticsLogger logger) {
    this.logger = logger;
  }

  @Override
  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {

    if (data != null) {
      UXCam.logEvent(eventName, data);
    } else {
      UXCam.logEvent(eventName);
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
