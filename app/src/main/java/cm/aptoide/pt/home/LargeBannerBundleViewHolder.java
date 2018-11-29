package cm.aptoide.pt.home;

import android.util.Log;
import android.view.View;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

class LargeBannerBundleViewHolder extends AppBundleViewHolder {
  private final MoPubView bannerView;
  private HomeAnalytics homeAnalytics;

  private boolean hasLoaded;

  public LargeBannerBundleViewHolder(View view, HomeAnalytics homeAnalytics) {
    super(view);
    bannerView = view.findViewById(R.id.banner);
    this.homeAnalytics = homeAnalytics;
    hasLoaded = false;

    bannerView.setBannerAdListener(new MoPubView.BannerAdListener() {
      @Override public void onBannerLoaded(MoPubView banner) {
        homeAnalytics.bannerImpression("MoPub");
      }

      @Override public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        Log.i("Mopub", errorCode.toString());
      }

      @Override public void onBannerClicked(MoPubView banner) {
        homeAnalytics.bannerClick("MoPub");
      }

      @Override public void onBannerExpanded(MoPubView banner) {

      }

      @Override public void onBannerCollapsed(MoPubView banner) {

      }
    });
    bannerView.setAdUnitId(BuildConfig.MOPUB_HOME_BANNER_PLACEMENT_ID);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    //BannerAdRequest bannerAdRequest = new BannerAdRequest();
    //bannerAdRequest.setCreativeType(BannerAdRequest.TYPE_STATIC);
    if (!hasLoaded) {
      hasLoaded = true;
      bannerView.loadAd();
    }
    //bannerView.setBannerListener(new BannerListener() {
    //  @Override public void adImpression() {
    //    super.adImpression();
    //    homeAnalytics.bannerImpression();
    //  }
    //
    //  @Override public void onAdClicked() {
    //    super.onAdClicked();
    //    homeAnalytics.bannerClick();
    //  }
    //});
    //bannerView.loadAd(bannerAdRequest);
  }

  public void destroyBanner() {
    bannerView.destroy();
  }
}
