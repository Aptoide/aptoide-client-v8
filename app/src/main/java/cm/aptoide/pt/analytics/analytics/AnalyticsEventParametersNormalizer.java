package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.analytics.KeyValueNormalizer;
import java.util.Map;

public class AnalyticsEventParametersNormalizer implements KeyValueNormalizer {
  @Override public Map<String, Object> normalize(Map<String, Object> data) {
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
