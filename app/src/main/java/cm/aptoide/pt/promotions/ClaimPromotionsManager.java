package cm.aptoide.pt.promotions;

import rx.Single;

public class ClaimPromotionsManager {

  private final String unclaimedAppPackageName;
  private final String promotionId;
  private PromotionsManager promotionsManager;

  public ClaimPromotionsManager(PromotionsManager promotionsManager, String unclaimedAppPackageName,
      String promotionId) {
    this.promotionsManager = promotionsManager;
    this.unclaimedAppPackageName = unclaimedAppPackageName;
    this.promotionId = promotionId;
  }

  public void saveWalletAddress(String walletAddress) {
    promotionsManager.saveWalletAddress(walletAddress);
  }

  public Single<ClaimStatusWrapper> claimPromotion() {
    return promotionsManager.claimPromotion(promotionsManager.getWalletAddress(),
        unclaimedAppPackageName, promotionId);
  }
}