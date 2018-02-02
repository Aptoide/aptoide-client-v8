package cm.aptoide.pt.analytics;

import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 27/06/17.
 */

public class FirstLaunchAnalytics {

  public static final String FIRST_LAUNCH = "Aptoide_First_Launch";
  private static final String UTM_SOURCE = "UTM_Source";
  private static final String UTM_MEDIUM = "UTM_Medium";
  private static final String UTM_CAMPAIGN = "UTM_Campaign";
  private static final String UTM_CONTENT = "UTM_Content";
  private static final String ENTRY_POINT = "Entry_Point";
  private final AnalyticsManager analyticsManager;

  public FirstLaunchAnalytics(AnalyticsManager analyticsManager) {
    this.analyticsManager = analyticsManager;
  }

  public void sendFirstLaunchEvent(String utmSource, String utmMedium, String utmCampaign,
      String utmContent, String entryPoint) {
    analyticsManager.logEvent(
        createFacebookFirstLaunchDataMap(utmSource, utmMedium, utmCampaign, utmContent, entryPoint),
        FIRST_LAUNCH, AnalyticsManager.Action.OPEN, "Launch");
  }

  private Map<String, Object> createFacebookFirstLaunchDataMap(String utmSource, String utmMedium,
      String utmCampaign, String utmContent, String entryPoint) {
    Map<String, Object> data = new HashMap<>();
    data.put(UTM_SOURCE, utmSource);
    data.put(UTM_MEDIUM, utmMedium);
    data.put(UTM_CAMPAIGN, utmCampaign);
    data.put(UTM_CONTENT, utmContent);
    data.put(ENTRY_POINT, entryPoint);
    return data;
  }
}
