package cm.aptoide.pt.app;

import cm.aptoide.pt.ads.model.AppNextNativeAd;
import cm.aptoide.pt.ads.model.ApplicationAdError;
import com.appnext.core.AppnextError;
import com.appnext.nativeads.NativeAd;

public class AppNextAdResult implements ApplicationAdResult {
  private final AppNextNativeAd ad;
  private final AppnextError error;

  public AppNextAdResult(NativeAd ad) {
    this.ad = new AppNextNativeAd(ad);
    this.error = null;
  }

  public AppNextAdResult(AppnextError error) {
    this.ad = null;
    this.error = error;
  }

  public AppNextNativeAd getAd() {
    return ad;
  }

  public ApplicationAdError getError() {
    if (error == null) return null;
    return new ApplicationAdError(error);
  }
}
