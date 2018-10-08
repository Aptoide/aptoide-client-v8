package cm.aptoide.pt.app;

import cm.aptoide.pt.ads.model.AppNextNativeAd;
import cm.aptoide.pt.ads.model.ApplicationAd;
import com.appnext.core.AppnextError;
import com.appnext.nativeads.NativeAd;

import cm.aptoide.pt.view.app.AppsList;

public class AppNextAdResult {
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

    public AppnextError getError() {
        return error;
    }
}
