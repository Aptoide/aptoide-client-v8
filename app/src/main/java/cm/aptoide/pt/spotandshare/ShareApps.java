package cm.aptoide.pt.spotandshare;

import cm.aptoide.pt.spotandshare.analytics.SpotAndShareAnalyticsInterface;
import lombok.Getter;

/**
 * Created by pedroribeiro on 03/03/17.
 */

public class ShareApps {

  @Getter private static SpotAndShareAnalyticsInterface analytics;

  public ShareApps(SpotAndShareAnalyticsInterface analytics) {
    ShareApps.analytics = analytics;
  }
}
