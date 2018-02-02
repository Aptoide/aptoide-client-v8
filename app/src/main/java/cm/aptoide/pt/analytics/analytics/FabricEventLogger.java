package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.logger.Logger;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import java.util.Map;
import java.util.Set;

/**
 * Created by trinkes on 11/01/2018.
 */

public class FabricEventLogger implements EventLogger {
  private static final String TAG = FabricEventLogger.class.getSimpleName();
  private final Answers fabric;

  public FabricEventLogger(Answers fabric) {
    this.fabric = fabric;
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
    fabric.logCustom(customEvent);
  }

  @Override public void setup() {

  }
}
