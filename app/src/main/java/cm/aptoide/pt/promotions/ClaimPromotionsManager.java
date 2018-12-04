package cm.aptoide.pt.promotions;

import rx.Single;

public class ClaimPromotionsManager {

  private PromotionsManager promotionsManager;
  private CaptchaService captchaService;
  private String walletAddress;

  public ClaimPromotionsManager(PromotionsManager promotionsManager) {
    this.promotionsManager = promotionsManager;
    walletAddress = null;
  }

  public void saveWalletAddres(String walletAddress) {
    this.walletAddress = walletAddress;
  }

  public Single<PromotionsManager.ClaimStatus> claimPromotion(String walletAddress,
      String packageName, String captcha) {
    return promotionsManager.claimPromotion(walletAddress, packageName, captcha);
  }
}
