package cm.aptoide.pt.ads;

import cm.aptoide.pt.blacklist.BlacklistManager;
import cm.aptoide.pt.install.PackageRepository;

import static cm.aptoide.pt.AptoideApplication.APPCOINS_WALLET_PACKAGE_NAME;

public class WalletAdsOfferCardManager {

  private final BlacklistManager blacklistManager;
  private final PackageRepository packageRepository;

  public WalletAdsOfferCardManager(BlacklistManager blacklistManager,
      PackageRepository packageRepository) {
    this.blacklistManager = blacklistManager;
    this.packageRepository = packageRepository;
  }

  public boolean shouldShowWalletOfferCard(String type, String id) {
    return !blacklistManager.isBlacklisted(type, id) && !packageRepository.isAppInstalled(
        APPCOINS_WALLET_PACKAGE_NAME);
  }
}
