package cm.aptoide.pt.promotions;

import rx.Single;

public class ClaimPromotionsManager {

  private final String unclaimedAppPackageName;
  private PromotionsManager promotionsManager;

  public ClaimPromotionsManager(PromotionsManager promotionsManager,
      String unclaimedAppPackageName) {
    this.promotionsManager = promotionsManager;
    this.unclaimedAppPackageName = unclaimedAppPackageName;
  }

  public void saveWalletAddress(String walletAddress) {
    promotionsManager.saveWalletAddress(walletAddress);
  }

  public Single<ClaimStatusWrapper> claimPromotion(String promotionId) {
    return promotionsManager.claimPromotion(promotionsManager.getWalletAddress(),
        unclaimedAppPackageName, promotionId);
  }
}