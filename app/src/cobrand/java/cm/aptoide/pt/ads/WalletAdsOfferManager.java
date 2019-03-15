package cm.aptoide.pt.ads;

import rx.Single;

public class WalletAdsOfferManager {

  public WalletAdsOfferManager() {
  }

  public Single<Boolean> shouldRequestMoPubAd() {
    return Single.just(false);
  }

  public enum OfferResponseStatus {
    NO_ADS, ADS_SHOW, ADS_HIDE
  }
}
