package cm.aptoide.pt.ads;

import cm.aptoide.pt.blacklist.BlacklistManager;
import cm.aptoide.pt.install.PackageRepository;

public class WalletAdsOfferCardProvider {

  private final BlacklistManager blacklistManager;
  private final PackageRepository packageRepository;

  public WalletAdsOfferCardProvider(BlacklistManager blacklistManager,
      PackageRepository packageRepository) {
    this.blacklistManager = blacklistManager;
    this.packageRepository = packageRepository;
  }

  public boolean shouldShowWalletOfferCard(String blacklistId) {
    return !blacklistManager.isBlacklisted(blacklistId) && !packageRepository.isAppInstalled(
        "com.appcoins.wallet");
  }
}
