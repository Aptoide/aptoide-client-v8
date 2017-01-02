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

  public void save(String md5, Report report) {
    map.put(md5, report);
  }

  public Report get(String md5) {
    return map.get(md5);
  }
}
