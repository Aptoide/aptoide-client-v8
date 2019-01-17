package cm.aptoide.pt.home;

import android.view.View;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.logger.Logger;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

public class CustomBannerBundleViewHolder extends AppBundleViewHolder {
  private final MoPubView bannerView;
  private final String TAG = "Mopub";
  private boolean hasLoaded;

  public CustomBannerBundleViewHolder(View itemView) {
    super(itemView);
    hasLoaded = false;
    bannerView = itemView.findViewById(R.id.banner);
    bannerView.setBannerAdListener(new MoPubView.BannerAdListener() {
      @Override public void onBannerLoaded(MoPubView banner) {
        Logger.getInstance()
            .d(TAG, "Custom Banner loaded");
      }

      @Override public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        Logger.getInstance()
            .e(TAG, "Custom Banner error : " + errorCode.toString());
      }

      @Override public void onBannerClicked(MoPubView banner) {
        Logger.getInstance()
            .d(TAG, "Custom Banner clicked");
      }

      @Override public void onBannerExpanded(MoPubView banner) {
        Logger.getInstance()
            .d(TAG, "Custom Banner expanded");
      }

      @Override public void onBannerCollapsed(MoPubView banner) {
        Logger.getInstance()
            .d(TAG, "Custom Banner collapsed");
      }
    });
    bannerView.setAdUnitId(BuildConfig.MOPUB_BANNER_90_HOME_PLACEMENT_ID_T12);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    if (!hasLoaded) {
      bannerView.loadAd();
      hasLoaded = true;
    }
  }
}
