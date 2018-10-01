package cm.aptoide.pt.app;

import com.appnext.core.AppnextError;
import com.appnext.nativeads.NativeAd;

import cm.aptoide.pt.view.app.AppsList;

public class AppNextAdResult {
    private final NativeAd minimalAd;
    private final AppnextError error;

    public AppNextAdResult(NativeAd minimalAd) {
        this.minimalAd = minimalAd;
        this.error = null;
    }

    public AppNextAdResult(AppnextError error) {
        this.minimalAd = null;
        this.error = error;
    }

    public NativeAd getNativeAd() {
        return minimalAd;
    }

    public AppnextError getError() {
        return error;
    }
}
