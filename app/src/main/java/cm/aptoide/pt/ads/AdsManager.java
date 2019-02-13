package cm.aptoide.pt.ads;

import cm.aptoide.pt.abtesting.experiments.MoPubBannerAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubInterstitialAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubNativeAdExperiment;
import rx.Single;

public class AdsManager {

  private MoPubInterstitialAdExperiment moPubInterstitialAdExperiment;
  private MoPubBannerAdExperiment moPubBannerAdExperiment;
  private MoPubNativeAdExperiment moPubNativeAdExperiment;

  public AdsManager(MoPubInterstitialAdExperiment moPubInterstitialAdExperiment,
      MoPubBannerAdExperiment moPubBannerAdExperiment,
      MoPubNativeAdExperiment moPubNativeAdExperiment) {
    this.moPubInterstitialAdExperiment = moPubInterstitialAdExperiment;
    this.moPubBannerAdExperiment = moPubBannerAdExperiment;
    this.moPubNativeAdExperiment = moPubNativeAdExperiment;
  }

  public Single<Boolean> shouldLoadInterstitialAd() {
    return moPubInterstitialAdExperiment.shouldLoadInterstitial();
  }

  public Single<Boolean> shouldLoadBannerAd() {
    return moPubBannerAdExperiment.shouldLoadBanner();
  }

  public Single<Boolean> shouldLoadNativeAds() {
    return moPubNativeAdExperiment.shouldLoadNative();
  }

  public Single<Boolean> recordInterstitialAdImpression() {
    return moPubInterstitialAdExperiment.recordAdImpression();
  }

  public Single<Boolean> recordInterstitialAdClick() {
    return moPubInterstitialAdExperiment.recordAdClick();
  }

  public Single<Boolean> recordBannerAdImpression() {
    return moPubBannerAdExperiment.recordAdImpression();
  }

  public Single<Boolean> recordBannerAdClick() {
    return moPubBannerAdExperiment.recordAdClick();
  }

  public Single<Boolean> recordNativeAdImpression() {
    return moPubBannerAdExperiment.recordAdImpression();
  }

  public Single<Boolean> recordNativeAdClick() {
    return moPubBannerAdExperiment.recordAdClick();
  }
}
