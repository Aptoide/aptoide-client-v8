package cm.aptoide.pt.app;

import com.appnext.nativeads.NativeAd;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.view.app.Application;

/**
 * Created by franciscocalado on 11/05/18.
 */

public class AppViewSimilarApp {

  private Application app;
  private NativeAd ad;

  public AppViewSimilarApp(Application app, NativeAd ad) {
    this.app = app;
    this.ad = ad;
  }

  public Application getApp() {
    return app;
  }

  public NativeAd getAd() {
    return ad;
  }

  public boolean isAd() {
    return app == null && ad != null;
  }
}
