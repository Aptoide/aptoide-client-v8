package cm.aptoide.pt.app;

import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.view.app.AptoideApp;

/**
 * Created by franciscocalado on 11/05/18.
 */

public class AppViewSimilarApp {

  private AptoideApp app;
  private ApplicationAd ad;

  public AppViewSimilarApp(AptoideApp app, ApplicationAd ad) {
    this.app = app;
    this.ad = ad;
  }

  public AptoideApp getApp() {
    return app;
  }

  public ApplicationAd getAd() {
    return ad;
  }

  public boolean isAd() {
    return app == null && ad != null;
  }

  public int getNetworkAdType() {
    if (ad == null) return 0;
    return ad.getNetwork()
        .ordinal();
  }
}
