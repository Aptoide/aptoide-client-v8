package cm.aptoide.pt.shareappsandroid;

import cm.aptoide.pt.shareappsandroid.analytics.SpotAndShareAnalyticsInterface;
import lombok.Getter;

/**
 * Created by pedroribeiro on 03/03/17.
 */

public class ShareApps {

  @Getter private static SpotAndShareAnalyticsInterface analytics;

  public ShareApps(SpotAndShareAnalyticsInterface analytics) {
    this.analytics = analytics;
  }
}
