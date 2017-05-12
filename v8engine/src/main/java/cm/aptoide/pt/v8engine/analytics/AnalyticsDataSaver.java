package cm.aptoide.pt.v8engine.analytics;

import android.support.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trinkes on 30/12/2016.
 */
public class AnalyticsDataSaver {
  private final Map<String, Event> map;

  public AnalyticsDataSaver() {
    map = new HashMap<>();
  }

  public void save(String key, Event event) {
    map.put(key + event.getClass()
        .getName(), event);
  }

  public Event get(String key) {
    return map.get(key);
  }

  public void remove(@NonNull Event event) {
    map.values()
        .remove(event);
  }
}
