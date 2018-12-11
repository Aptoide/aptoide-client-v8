package cm.aptoide.pt.app;

import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.ads.data.ApplicationAdError;
import cm.aptoide.pt.ads.data.AptoideNativeAd;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.view.app.AppsList;

/**
 * Created by D01 on 23/05/2018.
 */

public class MinimalAdRequestResult implements ApplicationAdResult {

  private final MinimalAd minimalAd;
  private final AppsList.Error error;

  public MinimalAdRequestResult(MinimalAd minimalAd) {
    this.minimalAd = minimalAd;
    this.error = null;
  }

  public MinimalAdRequestResult(AppsList.Error error) {
    this.minimalAd = null;
    this.error = error;
  }

  public MinimalAd getMinimalAd() {
    return minimalAd;
  }

  @Override public ApplicationAd getAd() {
    if (minimalAd == null) return null;
    return new AptoideNativeAd(minimalAd);
  }

  public ApplicationAdError getError() {
    if (error == null) return null;
    return new ApplicationAdError(error);
  }
}
