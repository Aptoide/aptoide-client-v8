package cm.aptoide.pt.ads;

import com.mopub.nativeads.MoPubNativeAdLoadedListener;

public class MoPubNativeAdsListener implements MoPubNativeAdLoadedListener {
  @Override public void onAdLoaded(int position) {
    // homeAnalytics.sendAdImpressionEvent(0, "Ad", position, "ads-highlighted", HomeEvent.Type.AD,
    //   ApplicationAd.Network.MOPUB);
  }

  @Override public void onAdRemoved(int position) {

  }
}
