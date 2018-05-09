package cm.aptoide.pt.app;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import java.util.List;

/**
 * Created by D01 on 07/05/18.
 */

public class AdsViewModel {

  private final MinimalAd ad;
  private final List<App> recommendedApps;

  public AdsViewModel(MinimalAd ad, List<App> recommendedApps) {
    this.ad = ad;
    this.recommendedApps = recommendedApps;
  }

  public MinimalAd getAd() {
    return ad;
  }

  public List<App> getRecommendedApps() {
    return recommendedApps;
  }
}
