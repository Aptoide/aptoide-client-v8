package cm.aptoide.pt.ads;

import rx.Single;

public class MoPubAdsManager {

  private final WalletAdsOfferManager walletAdsOfferManager;

  public MoPubAdsManager(WalletAdsOfferManager walletAdsOfferManager) {
    this.walletAdsOfferManager = walletAdsOfferManager;
  }

  public Single<WalletAdsOfferManager.OfferResponseStatus> getAdsVisibilityStatus() {
    return shouldRequestAds().flatMap(shouldRequestAds -> {
      if (shouldRequestAds) {
        return Single.just(WalletAdsOfferManager.OfferResponseStatus.NO_ADS);
      } else {
        return Single.just(WalletAdsOfferManager.OfferResponseStatus.ADS_HIDE);
      }
    });
  }

  public Single<Boolean> shouldRequestAds() {
    return walletAdsOfferManager.shouldRequestMoPubAd();
  }
}
