package cm.aptoide.pt.app;

import cm.aptoide.analytics.AnalyticsManager;
import java.util.Map;

public class CampaignAnalytics {
  private final Map<String, CampaignEvent> cache;
  private final AnalyticsManager analyticsManager;

  public CampaignAnalytics(Map<String, CampaignEvent> cache, AnalyticsManager analyticsManager) {
    this.cache = cache;
    this.analyticsManager = analyticsManager;
  }

  public void sendCampaignConversionEvent(String url, String packageName, int vercode) {
    CampaignEvent event = new CampaignEvent(url, packageName, vercode);
    cache.put(packageName + String.valueOf(vercode), event);
  }

  public void convertCampaignEvent(String packageName, int vercode) {
    String cacheKey = packageName + String.valueOf(vercode);
    if (cache.containsKey(cacheKey)) {
      analyticsManager.logEvent(cache.get(cacheKey)
          .getUrl());
    }
  }

  public static class CampaignEvent {
    private String url;
    private String packageName;
    private int vercode;

    public CampaignEvent(String url, String packageName, int vercode) {
      this.url = url;
      this.packageName = packageName;
      this.vercode = vercode;
    }

    public String getUrl() {
      return url;
    }

    public String getPackageName() {
      return packageName;
    }

    public int getVercode() {
      return vercode;
    }
  }
}
