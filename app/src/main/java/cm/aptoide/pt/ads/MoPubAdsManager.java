package cm.aptoide.pt.ads;

import rx.Single;

public class MoPubAdsManager {

  private final WalletAdsOfferManager walletAdsOfferManager;
  private final MoPubConsentDialogManager moPubConsentDialogManager;

  public MoPubAdsManager(WalletAdsOfferManager walletAdsOfferManager,
      MoPubConsentDialogManager moPubConsentDialogManager) {
    this.walletAdsOfferManager = walletAdsOfferManager;
    this.moPubConsentDialogManager = moPubConsentDialogManager;
  }

  public Single<WalletAdsOfferManager.OfferResponseStatus> getAdsVisibilityStatus() {
    return Single.just(WalletAdsOfferManager.OfferResponseStatus.ADS_HIDE);
  }

  public Single<Boolean> shouldLoadBannerAd() {
    return shouldShowAds();
  }

  public Single<Boolean> shouldLoadNativeAds() {
    return shouldShowAds();
  }

  public Single<Boolean> shouldShowAds() {
    return walletAdsOfferManager.shouldRequestMoPubAd();
  }

  public Single<Boolean> shouldShowConsentDialog() {
    return moPubConsentDialogManager.shouldShowConsentDialog();
  }
}
