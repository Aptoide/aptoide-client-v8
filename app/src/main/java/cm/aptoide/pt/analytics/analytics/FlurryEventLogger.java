package cm.aptoide.pt.analytics.analytics;

import com.flurry.android.FlurryAgent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 11/01/2018.
 */

public class FlurryEventLogger implements EventLogger {
  private static final String TAG = FlurryEventLogger.class.getSimpleName();

  @Override
  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    if (data != null) {
      FlurryAgent.logEvent(eventName, map(data));
    } else {
      FlurryAgent.logEvent(eventName);
    }
  }

  @Override public void setup() {

  }

  private Map<String, String> map(Map<String, Object> data) {
    Map<String, String> map = new HashMap<>();
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      if (entry.getValue() != null) {
        map.put(entry.getKey(), entry.getValue()
            .toString());
      }
    }
    return map;
  }
}
