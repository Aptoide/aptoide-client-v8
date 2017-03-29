package cm.aptoide.pt.spotandshareandroid;

import cm.aptoide.pt.spotandshareandroid.analytics.SpotAndShareAnalyticsInterface;
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
