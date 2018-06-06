package cm.aptoide.pt.analytics.analytics;

import java.util.Map;

class AnalyticsEventRequestBody {
  private final String aptoidePackage;
  private final Map<String, Object> data;
  private final String timestamp;

  public AnalyticsEventRequestBody(String aptoidePackage, Map<String, Object> data,
      String timestamp) {
    super();
    this.aptoidePackage = aptoidePackage;
    this.data = data;
    this.timestamp = timestamp;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public String getAptoidePackage() {
    return aptoidePackage;
  }
}
