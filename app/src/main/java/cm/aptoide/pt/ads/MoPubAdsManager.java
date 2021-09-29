package cm.aptoide.pt.ads;

import rx.Single;

public class MoPubAdsManager {

  private final WalletAdsOfferManager walletAdsOfferManager;
  private final MoPubConsentDialogManager moPubConsentDialogManager;
  private final AdsExperiment adsExperiment;

  public MoPubAdsManager(WalletAdsOfferManager walletAdsOfferManager,
      MoPubConsentDialogManager moPubConsentDialogManager, AdsExperiment adsExperiment) {
    this.walletAdsOfferManager = walletAdsOfferManager;
    this.moPubConsentDialogManager = moPubConsentDialogManager;
    this.adsExperiment = adsExperiment;
  }

  public Single<WalletAdsOfferManager.OfferResponseStatus> getAdsVisibilityStatus() {
    return shouldRequestAds().flatMap(shouldRequestAds -> {
      if (shouldRequestAds) {
        return shouldShowAds().flatMap(abTestHasAds -> {
          if (abTestHasAds) {
            return Single.just(WalletAdsOfferManager.OfferResponseStatus.ADS_SHOW);
          } else {
            return Single.just(WalletAdsOfferManager.OfferResponseStatus.NO_ADS);
          }
        });
      }
      return Single.just(WalletAdsOfferManager.OfferResponseStatus.ADS_HIDE);
    });
/*
    shouldRequestAds ? Single.just(WalletAdsOfferManager.OfferResponseStatus.ADS_SHOW)
        : Single.just(WalletAdsOfferManager.OfferResponseStatus.ADS_HIDE));*/
  }

  public Single<Boolean> shouldLoadBannerAd() {
    return shouldRequestAds().flatMap(shouldRequestAds -> {
      if (shouldRequestAds) {
        return shouldShowAds();
      } else {
        return Single.just(false);
      }
    });
  }

  public Single<Boolean> shouldLoadNativeAds() {
    return shouldRequestAds().flatMap(shouldRequestAds -> {
      if (shouldRequestAds) {
        return shouldShowAds();
      } else {
        return Single.just(false);
      }
    });
  }

  public Single<Boolean> shouldRequestAds() {
    return walletAdsOfferManager.shouldRequestMoPubAd();
  }

  public Single<Boolean> shouldShowConsentDialog() {
    return moPubConsentDialogManager.shouldShowConsentDialog();
  }

  public Single<Boolean> shouldShowAds() {
    return adsExperiment.shouldLoadAds();
  }
}
