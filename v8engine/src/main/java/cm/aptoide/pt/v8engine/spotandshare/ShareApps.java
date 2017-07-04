package cm.aptoide.pt.v8engine.spotandshare;

import cm.aptoide.pt.v8engine.spotandshare.analytics.SpotAndShareAnalyticsInterface;
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
