package cm.aptoide.pt.app;

import cm.aptoide.pt.ads.model.ApplicationAd;

import cm.aptoide.pt.view.app.Application;

/**
 * Created by franciscocalado on 11/05/18.
 */

public class AppViewSimilarApp {

  private Application app;
  private ApplicationAd ad;

  public AppViewSimilarApp(Application app, ApplicationAd ad) {
    this.app = app;
    this.ad = ad;
  }

  public Application getApp() {
    return app;
  }

  public ApplicationAd getAd() {
    return ad;
  }

  public boolean isAd() {
    return app == null && ad != null;
  }
}
