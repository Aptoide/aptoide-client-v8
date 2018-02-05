package cm.aptoide.pt.analytics.analytics;

import android.os.Bundle;
import cm.aptoide.pt.logger.Logger;
import com.facebook.appevents.AppEventsLogger;
import java.util.Map;

/**
 * Created by trinkes on 11/01/2018.
 */

public class FacebookEventLogger implements EventLogger {
  private static final String TAG = FacebookEventLogger.class.getSimpleName();
  private final AppEventsLogger facebook;

  public FacebookEventLogger(AppEventsLogger facebook) {
    this.facebook = facebook;
  }

  @Override
  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    if (data != null) {
      facebook.logEvent(eventName, mapToBundle(data));
    } else {
      facebook.logEvent(eventName);
    }
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

  @Override public void setup() {

  }

  private Bundle mapToBundle(Map<String, Object> data) {
    Bundle bundle = new Bundle();
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      bundle.putString(entry.getKey(), entry.getValue()
          .toString());
    }
    return bundle;
  }
}
