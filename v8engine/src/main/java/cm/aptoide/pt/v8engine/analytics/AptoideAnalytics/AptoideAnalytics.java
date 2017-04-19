package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics;

import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 17/04/17.
 */

public class AptoideAnalytics {

  protected Bundle createBundleData(String key, String value) {
    final Bundle data = new Bundle();
    data.putString(key, value);
    return data;
  }

  //TODO: To delete when localytics is killed
  protected Map<String, String> createMapData(String key, String value) {
    final Map<String, String> data = new HashMap<>();
    data.put(key, value);
    return data;
  }

  protected Bundle createComplexBundleData(Map<String, Object> keyValuePair) {
    Bundle bundle = new Bundle();
    for (Map.Entry<String, Object> entry : keyValuePair.entrySet()) {
      bundle.putString(entry.getKey(), (String) entry.getValue());
    }
    return bundle;
  }
}
