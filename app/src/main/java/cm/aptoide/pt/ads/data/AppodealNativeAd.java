package cm.aptoide.pt.ads.data;

import android.view.View;
import cm.aptoide.pt.BuildConfig;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeAdView;

public class AppodealNativeAd implements ApplicationAd {
  private final NativeAd nativeAd;

  public AppodealNativeAd(NativeAd nativeAd) {
    this.nativeAd = nativeAd;
  }

  @Override public String getAdTitle() {
    return nativeAd.getTitle();
  }

  @Override public String getIconUrl() {
    return nativeAd.getIconUrl();
  }

  @Override public Integer getStars() {
    return 0;
  }

  @Override public void registerClickableView(View view) {
    ((NativeAdView) view).registerView(nativeAd, BuildConfig.APPODEAL_HIGHLIGHTED_PLACEMENT_T6_ID);
  }

  @Override public String getPackageName() {
    return "";
  }

  @Override public Network getNetwork() {
    return Network.APPODEAL;
  }

  @Override public void setAdView(View adView) {

  }
}
