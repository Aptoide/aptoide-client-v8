package cm.aptoide.pt.ads.model;

import android.view.View;
import com.appnext.nativeads.NativeAd;

/**
 * Created by franciscoaleixo on 04/10/2018.
 */

public class AppNextNativeAd implements ApplicationAd{
  private final NativeAd nativeAd;

  public AppNextNativeAd(NativeAd nativeAd) {
    this.nativeAd = nativeAd;
  }

  @Override public String getAdTitle() {
    return nativeAd.getAdTitle();
  }

  @Override public String getIconUrl() {
    return nativeAd.getIconURL();
  }

  @Override public Integer getStars() {
    return 0;
  }

  @Override public void registerClickableView(View view) {
    nativeAd.registerClickableViews(view);
  }

  @Override public String getPackageName() {
    return nativeAd.getAppPackageName();
  }

  @Override public Network getNetwork() {
    return Network.APPNEXT;
  }
}
