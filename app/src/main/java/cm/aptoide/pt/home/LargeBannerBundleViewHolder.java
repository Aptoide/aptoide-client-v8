package cm.aptoide.pt.home;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import com.appodeal.ads.Appodeal;

class LargeBannerBundleViewHolder extends AppBundleViewHolder {
  private final Activity activity;
  //private final MoPubView bannerView;
  private HomeAnalytics homeAnalytics;
  private boolean hasLoaded;

  public LargeBannerBundleViewHolder(View view, HomeAnalytics homeAnalytics, Activity activity) {
    super(view);
    //bannerView = view.findViewById(R.id.banner);
    this.homeAnalytics = homeAnalytics;
    hasLoaded = false;
    this.activity = activity;

    ((LinearLayout) view).addView(Appodeal.getMrecView(activity));
    //((LinearLayout) view).addView(Appodeal.getBannerView(activity));
    //Appodeal.getBannerView(activity);

    //bannerView.setBannerAdListener(new MoPubView.BannerAdListener() {
    //  @Override public void onBannerLoaded(MoPubView banner) {
    //    homeAnalytics.bannerImpression("MoPub");
    //  }
    //
    //  @Override public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
    //    Log.i("Mopub", errorCode.toString());
    //  }
    //
    //  @Override public void onBannerClicked(MoPubView banner) {
    //    homeAnalytics.bannerClick("MoPub");
    //  }
    //
    //  @Override public void onBannerExpanded(MoPubView banner) {
    //
    //  }
    //
    //  @Override public void onBannerCollapsed(MoPubView banner) {
    //
    //  }
    //});
    //bannerView.setAdUnitId(BuildConfig.MOPUB_HOME_BANNER_PLACEMENT_ID);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    //BannerAdRequest bannerAdRequest = new BannerAdRequest();
    //bannerAdRequest.setCreativeType(BannerAdRequest.TYPE_STATIC);
    if (!hasLoaded) {
      hasLoaded = true;
      Appodeal.show(activity, Appodeal.MREC);
      //Appodeal.show(activity, Appodeal.BANNER_VIEW);
      //bannerView.loadAd();
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
    //bannerView.destroy();
  }
}
