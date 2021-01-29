package cm.aptoide.pt.ads;

import android.content.pm.PackageInfo;
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
    return Single.just(false);
  }

  private boolean isWalletInstalled() {
    try {
      final PackageInfo packageInfo = packageManager.getPackageInfo(WALLET_PACKAGE_NAME, 0);
      return packageInfo != null;
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
  }

  public enum OfferResponseStatus {
    NO_ADS, ADS_SHOW, ADS_HIDE
  }
}
