package cm.aptoide.pt.spotandshare;

import cm.aptoide.pt.spotandshare.analytics.SpotAndShareAnalyticsInterface;

/**
 * Created by pedroribeiro on 03/03/17.
 */

public class ShareApps {

  private static SpotAndShareAnalyticsInterface analytics;

  public static SpotAndShareAnalyticsInterface getAnalytics() {
    return analytics;
  }

  public ShareApps(SpotAndShareAnalyticsInterface analytics) {
    ShareApps.analytics = analytics;
  }
}
