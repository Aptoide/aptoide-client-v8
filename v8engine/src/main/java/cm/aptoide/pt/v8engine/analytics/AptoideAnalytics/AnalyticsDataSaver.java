package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 30/12/2016.
 */
public class AnalyticsDataSaver {
  Map<String, Event> map;

  public AnalyticsDataSaver() {
    map = new HashMap<>();
  }

  public void save(String key, Event event) {
    map.put(key, event);
  }

  public Event get(String key) {
    return map.get(key);
  }

  public void remove(String key) {
    map.remove(key);
  }
}
