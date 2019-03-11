package cm.aptoide.pt.ads;

import cm.aptoide.pt.abtesting.experiments.MoPubBannerAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubInterstitialAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubNativeAdExperiment;
import rx.Single;

public class MoPubAdsManager {

  private final MoPubInterstitialAdExperiment moPubInterstitialAdExperiment;
  private final MoPubBannerAdExperiment moPubBannerAdExperiment;
  private final MoPubNativeAdExperiment moPubNativeAdExperiment;
  private final WalletAdsOfferManager walletAdsOfferManager;

  public MoPubAdsManager(MoPubInterstitialAdExperiment moPubInterstitialAdExperiment,
      MoPubBannerAdExperiment moPubBannerAdExperiment,
      MoPubNativeAdExperiment moPubNativeAdExperiment,
      WalletAdsOfferManager walletAdsOfferManager) {
    this.moPubInterstitialAdExperiment = moPubInterstitialAdExperiment;
    this.moPubBannerAdExperiment = moPubBannerAdExperiment;
    this.moPubNativeAdExperiment = moPubNativeAdExperiment;
    this.walletAdsOfferManager = walletAdsOfferManager;
  }

  public Single<Boolean> shouldLoadInterstitialAd() {
    return areAdsBlockedByWalletOffer().flatMap(requestInterstitialAd -> {
      if (requestInterstitialAd) {
        return moPubInterstitialAdExperiment.shouldLoadInterstitial();
      }
      return Single.just(false);
    });
  }

  public Single<Boolean> shouldLoadBannerAd() {
    return areAdsBlockedByWalletOffer().flatMap(requestInterstitialAd -> {
      if (requestInterstitialAd) {
        return moPubBannerAdExperiment.shouldLoadBanner();
      }
      return Single.just(false);
    });
  }

  public Single<Boolean> shouldLoadNativeAds() {
    return areAdsBlockedByWalletOffer().flatMap(requestInterstitialAd -> {
      if (requestInterstitialAd) {
        return moPubNativeAdExperiment.shouldLoadNative();
      }
      return Single.just(false);
    });
  }

  public Single<Boolean> recordInterstitialAdImpression() {
    return moPubInterstitialAdExperiment.recordAdImpression();
  }

  public Single<Boolean> recordInterstitialAdClick() {
    return moPubInterstitialAdExperiment.recordAdClick();
  }

  public Single<Boolean> areAdsBlockedByWalletOffer() {
    return walletAdsOfferManager.shouldRequestMoPubAd();
  }
}
