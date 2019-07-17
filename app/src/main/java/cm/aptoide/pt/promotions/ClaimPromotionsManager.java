package cm.aptoide.pt.promotions;

import rx.Single;

public class ClaimPromotionsManager {

  private PromotionsManager promotionsManager;

  public ClaimPromotionsManager(PromotionsManager promotionsManager) {
    this.promotionsManager = promotionsManager;
  }

  public void saveWalletAddress(String walletAddress) {
    promotionsManager.saveWalletAddress(walletAddress);
  }

  public Single<ClaimStatusWrapper> claimPromotion(String packageName, String captcha,
      String promotionId) {
    return promotionsManager.claimPromotion(promotionsManager.getWalletAddress(), packageName,
        captcha, promotionId);
  }
}
