package cm.aptoide.pt.ads;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import rx.Single;

public class WalletAdsOfferManager {

  private static final String WALLET_PACKAGE_NAME = "com.appcoins.wallet";
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

  public enum OfferResponseStatus {
    NO_ADS, ADS_UNLOCKED, ADS_LOCKED
  }
}
