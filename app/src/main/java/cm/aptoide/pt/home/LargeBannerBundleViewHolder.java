package cm.aptoide.pt.home;

import android.view.View;
import cm.aptoide.pt.R;
import com.appnext.banners.BannerAdRequest;
import com.mopub.mobileads.MoPubView;

class LargeBannerBundleViewHolder extends AppBundleViewHolder {
  private final MoPubView bannerView;
  private HomeAnalytics homeAnalytics;

  public LargeBannerBundleViewHolder(View view, HomeAnalytics homeAnalytics) {
    super(view);
    bannerView = view.findViewById(R.id.banner);
    this.homeAnalytics = homeAnalytics;
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    BannerAdRequest bannerAdRequest = new BannerAdRequest();
    bannerAdRequest.setCreativeType(BannerAdRequest.TYPE_STATIC);
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
}
