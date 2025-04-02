package cm.aptoide.analytics.implementation.loggers;

import android.os.Bundle;
import cm.aptoide.analytics.AnalyticsLogger;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.EventLogger;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.Map;

public class FirebaseEventLogger implements EventLogger {
  private static final String TAG = "FirebaseEventLogger";
  private final AnalyticsLogger logger;
  private final FirebaseAnalytics firebaseAnalytics;

  public FirebaseEventLogger(FirebaseAnalytics firebaseAnalytics, AnalyticsLogger logger) {
    this.firebaseAnalytics = firebaseAnalytics;
    this.logger = logger;
  }

  @Override
  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    firebaseAnalytics.logEvent(eventName, mapToBundle(data));
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
