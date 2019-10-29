package cm.aptoide.pt.ads;

import android.content.pm.PackageManager;
import cm.aptoide.pt.blacklist.BlacklistManager;
import cm.aptoide.pt.wallet.WalletPackageManager;

public class WalletAdsOfferCardManager {

  private final BlacklistManager blacklistManager;
  private final PackageManager packageManager;

  public WalletAdsOfferCardManager(BlacklistManager blacklistManager,
      PackageManager packageManager) {
    this.blacklistManager = blacklistManager;
    this.packageManager = packageManager;
  }

  public boolean shouldShowWalletOfferCard(String type, String id) {
    return !blacklistManager.isBlacklisted(type, id) && !new WalletPackageManager(
        packageManager).isThereAPackageToProcessAPPCPayments();
  }
}
