package cm.aptoide.pt.home;

import android.view.View;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.MoPubBannerAdListener;
import com.mopub.mobileads.MoPubView;

class SmallBannerAdBundleViewHolder extends AppBundleViewHolder {

  private final MoPubView bannerView;
  private boolean hasLoaded;

  public SmallBannerAdBundleViewHolder(View view) {
    super(view);
    hasLoaded = false;
    bannerView = view.findViewById(R.id.banner);
    bannerView.setBannerAdListener(new MoPubBannerAdListener());
    bannerView.setAdUnitId(BuildConfig.MOPUB_BANNER_50_HOME_PLACEMENT_ID);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    if (!hasLoaded) {
      bannerView.loadAd();
      hasLoaded = true;
    }
  }
}
