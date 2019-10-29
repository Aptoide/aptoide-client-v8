package cm.aptoide.pt.ads;

import android.content.pm.PackageManager;
import cm.aptoide.pt.wallet.WalletPackageManager;
import rx.Single;

public class WalletAdsOfferManager {

  private final PackageManager packageManager;
  private final WalletAdsOfferService walletAdsOfferService;

  public WalletAdsOfferManager(PackageManager packageManager,
      WalletAdsOfferService walletAdsOfferService) {
    this.packageManager = packageManager;
    this.walletAdsOfferService = walletAdsOfferService;
  }

  public Single<Boolean> shouldRequestMoPubAd() {
    return walletAdsOfferService.isWalletOfferActive()
        .flatMap(isOfferActive -> {
          if (isOfferActive) {
            return Single.just(
                !new WalletPackageManager(packageManager).isThereAPackageToProcessAPPCPayments());
          } else {
            return Single.just(true);
          }
        });
  }

  public enum OfferResponseStatus {
    NO_ADS, ADS_SHOW, ADS_HIDE
  }
}
