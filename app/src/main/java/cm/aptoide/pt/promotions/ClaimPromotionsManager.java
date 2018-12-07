package cm.aptoide.pt.promotions;

import rx.Single;

public class ClaimPromotionsManager {

  private PromotionsManager promotionsManager;
  private CaptchaService captchaService;
  private String walletAddress;

  public ClaimPromotionsManager(PromotionsManager promotionsManager,
      CaptchaService captchaService) {
    this.promotionsManager = promotionsManager;
    this.captchaService = captchaService;
    walletAddress = null;
  }

  public Single<String> getCaptcha(String userId) {
    return captchaService.getCaptcha(userId);
  }

  public void saveWalletAddress(String walletAddress) {
    this.walletAddress = walletAddress;
  }

  public Single<ClaimStatusWrapper> claimPromotion(String packageName, String captcha) {
    return promotionsManager.claimPromotion(walletAddress, packageName, captcha);
  }

  public void saveCaptchaUrl(String captchaUrl) {
    promotionsManager.saveCaptchaUrl(captchaUrl);
  }

  public String getCaptchaUrl() {
    return promotionsManager.getCaptchaUrl();
  }
}
