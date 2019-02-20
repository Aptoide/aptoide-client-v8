package cm.aptoide.pt.ads;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import cm.aptoide.pt.abtesting.experiments.MoPubBannerAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubInterstitialAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubNativeAdExperiment;
import rx.Single;

public class MoPubAdsManager {

  private static final String WALLET_PACKAGE_NAME = "com.appcoins.wallet";
  private final MoPubInterstitialAdExperiment moPubInterstitialAdExperiment;
  private final MoPubBannerAdExperiment moPubBannerAdExperiment;
  private final MoPubNativeAdExperiment moPubNativeAdExperiment;
  private final PackageManager packageManager;
  private final WalletAdsOfferService walletAdsOfferService;

  public MoPubAdsManager(MoPubInterstitialAdExperiment moPubInterstitialAdExperiment,
      MoPubBannerAdExperiment moPubBannerAdExperiment,
      MoPubNativeAdExperiment moPubNativeAdExperiment, PackageManager packageManager,
      WalletAdsOfferService walletAdsOfferService) {
    this.moPubInterstitialAdExperiment = moPubInterstitialAdExperiment;
    this.moPubBannerAdExperiment = moPubBannerAdExperiment;
    this.moPubNativeAdExperiment = moPubNativeAdExperiment;
    this.packageManager = packageManager;
    this.walletAdsOfferService = walletAdsOfferService;
  }

  public Single<Boolean> shouldLoadInterstitialAd() {
    return shouldRequestMoPubAd().flatMap(requestInterstitialAd -> {
      if (requestInterstitialAd) {
        return moPubInterstitialAdExperiment.shouldLoadInterstitial();
      }
      return Single.just(false);
    });
  }

  public Single<Boolean> shouldLoadBannerAd() {
    return shouldRequestMoPubAd().flatMap(requestInterstitialAd -> {
      if (requestInterstitialAd) {
        return moPubBannerAdExperiment.shouldLoadBanner();
      }
      return Single.just(false);
    });
  }

  public Single<Boolean> shouldLoadNativeAds() {
    return shouldRequestMoPubAd().flatMap(requestInterstitialAd -> {
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

  private Single<Boolean> shouldRequestMoPubAd() {
    return walletAdsOfferService.isWalletOfferActive()
        .flatMap(isOfferActive -> {
          if (isOfferActive) {
            return Single.just(!isWalletInstalled());
          } else {
            return Single.just(true);
          }
        });
  }

  private boolean isWalletInstalled() {
    for (ApplicationInfo applicationInfo : packageManager.getInstalledApplications(0)) {
      if (applicationInfo.packageName.equals(WALLET_PACKAGE_NAME)) {
        return true;
      }
    }
    return false;
  }
}
