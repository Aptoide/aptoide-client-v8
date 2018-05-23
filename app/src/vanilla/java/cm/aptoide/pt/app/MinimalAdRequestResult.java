package cm.aptoide.pt.app;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.view.app.AppsList;

/**
 * Created by D01 on 23/05/2018.
 */

public class MinimalAdRequestResult {

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

  public AppsList.Error getError() {
    return error;
  }
}
