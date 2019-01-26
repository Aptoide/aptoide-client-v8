package cm.aptoide.pt.ads;

import cm.aptoide.pt.logger.Logger;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

public class MoPubBannerAdListener implements MoPubView.BannerAdListener {

  @Override public void onBannerLoaded(MoPubView banner) {
    Logger.getInstance()
        .d("Mopub", "Banner loaded");
  }

  @Override public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
    Logger.getInstance()
        .e("Mopub", "Banner error : " + errorCode.toString());
  }

  @Override public void onBannerClicked(MoPubView banner) {
    Logger.getInstance()
        .d("Mopub", "Banner clicked");
  }

  @Override public void onBannerExpanded(MoPubView banner) {
    Logger.getInstance()
        .d("Mopub", "Banner expanded");
  }

  @Override public void onBannerCollapsed(MoPubView banner) {
    Logger.getInstance()
        .d("Mopub", "Banner collapsed");
  }
}
