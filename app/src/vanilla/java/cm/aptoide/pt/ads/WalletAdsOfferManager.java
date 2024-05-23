package cm.aptoide.pt.ads;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import rx.Single;

import static cm.aptoide.pt.AptoideApplication.APPCOINS_WALLET_PACKAGE_NAME;

public class WalletAdsOfferManager {
  private final PackageManager packageManager;

  public WalletAdsOfferManager(PackageManager packageManager) {
    this.packageManager = packageManager;
  }

  public Single<Boolean> shouldRequestMoPubAd() {
    return Single.just(!isWalletInstalled());
  }

  private boolean isWalletInstalled() {
    try {
      final PackageInfo packageInfo =
          packageManager.getPackageInfo(APPCOINS_WALLET_PACKAGE_NAME, 0);
      return packageInfo != null;
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
  }

  public enum OfferResponseStatus {
    NO_ADS, ADS_SHOW, ADS_HIDE
  }
}
