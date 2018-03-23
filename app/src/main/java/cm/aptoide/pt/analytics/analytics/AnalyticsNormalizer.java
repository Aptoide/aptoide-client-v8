package cm.aptoide.pt.analytics.analytics;

import java.util.Map;

/**
 * Created by jdandrade on 27/02/2018.
 */

public class AnalyticsNormalizer {
  Map<String, Object> normalize(Map<String, Object> data) {
    if (data == null) {
      return null;
    }
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      if (entry.getValue() == null) {
        entry.setValue("");
      }
    }
    return data;
  }
}
