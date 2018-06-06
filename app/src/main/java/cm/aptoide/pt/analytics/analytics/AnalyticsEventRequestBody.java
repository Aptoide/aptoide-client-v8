package cm.aptoide.pt.analytics.analytics;

import java.util.Map;

class AnalyticsEventRequestBody extends AnalyticsBaseBody {
  private final Map<String, Object> data;
  private final String timestamp;

  public AnalyticsEventRequestBody(Map<String, Object> data, String timestamp) {
    this.data = data;
    this.timestamp = timestamp;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public Map<String, Object> getData() {
    return data;
  }
}
