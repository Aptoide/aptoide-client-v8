package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.logger.Logger;
import java.util.Map;

/**
 * Created by trinkes on 11/01/2018.
 */

public class FacebookEventLogger implements EventLogger {
  private static final String TAG = FacebookEventLogger.class.getSimpleName();

  @Override
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
