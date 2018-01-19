package cm.aptoide.pt.analytics;

import android.support.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 30/12/2016.
 */
public class AnalyticsDataSaver {
  private final Map<String, cm.aptoide.pt.analytics.analytics.Event> map;
  private final Map<String, cm.aptoide.pt.analytics.Event> oldmap;

  public AnalyticsDataSaver() {
    map = new HashMap<>();
    oldmap = new HashMap<>();
  }

  public void save(String key, cm.aptoide.pt.analytics.analytics.Event event) {
    map.put(key, event);
  }

  public cm.aptoide.pt.analytics.analytics.Event newGet(String key) {
    return map.get(key);
  }

  public void save(String key, cm.aptoide.pt.analytics.Event event) {
    oldmap.put(key, event);
  }

  public cm.aptoide.pt.analytics.Event get(String key) {
    return oldmap.get(key);
  }

  public void remove(@NonNull Event event) {
    map.values()
        .remove(event);
  }
}
