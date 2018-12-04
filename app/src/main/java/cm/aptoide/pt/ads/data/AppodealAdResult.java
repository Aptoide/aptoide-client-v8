package cm.aptoide.pt.ads.data;

import cm.aptoide.pt.app.ApplicationAdResult;
import com.appodeal.ads.NativeAd;

public class AppodealAdResult implements ApplicationAdResult {
  private final AppodealNativeAd ad;
  private final boolean error;

  public AppodealAdResult(NativeAd ad) {
    this.ad = new AppodealNativeAd(ad);
    this.error = false;
  }

  public AppodealAdResult() {
    this.ad = null;
    this.error = true;
  }

  public AppodealNativeAd getAd() {
    return ad;
  }

  public ApplicationAdError getError() {
    if (!error) return null;
    return new ApplicationAdError();
  }
}
