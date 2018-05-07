package cm.aptoide.pt.app;

import cm.aptoide.pt.database.realm.MinimalAd;
import java.util.List;

/**
 * Created by D01 on 07/05/18.
 */

public class AdsViewModel {

  private final List<MinimalAd> minimalAds;

  public AdsViewModel(List<MinimalAd> minimalAds) {

    this.minimalAds = minimalAds;
  }

  public List<MinimalAd> getMinimalAds() {
    return minimalAds;
  }
}
