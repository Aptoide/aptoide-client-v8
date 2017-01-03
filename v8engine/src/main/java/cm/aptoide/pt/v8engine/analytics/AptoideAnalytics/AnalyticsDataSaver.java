package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 30/12/2016.
 */
public class AnalyticsDataSaver {
  Map<String, Report> map;

  public AnalyticsDataSaver() {
    map = new HashMap<>();
  }

  public void save(String key, Report report) {
    map.put(key, report);
  }

  public Report get(String key) {
    return map.get(key);
  }

  public void remove(String key) {
    map.remove(key);
  }
}
