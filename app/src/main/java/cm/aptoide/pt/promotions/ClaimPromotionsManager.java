package cm.aptoide.pt.promotions;

import rx.Single;

public class ClaimPromotionsManager {

  private PromotionsManager promotionsManager;
  private CaptchaService captchaService;

  public ClaimPromotionsManager(PromotionsManager promotionsManager,
      CaptchaService captchaService) {
    this.promotionsManager = promotionsManager;
    this.captchaService = captchaService;
  }

  public Single<String> getCaptcha() {
    return captchaService.getCaptcha();
  }

  public void saveWalletAddress(String walletAddress) {
    promotionsManager.saveWalletAddress(walletAddress);
  }

  public Single<ClaimStatusWrapper> claimPromotion(String packageName, String captcha) {
    return promotionsManager.claimPromotion(promotionsManager.getWalletAddress(), packageName,
        captcha);
  }

  public void saveCaptchaUrl(String captchaUrl) {
    captchaService.saveCaptchaUrl(captchaUrl);
  }

  public String getCaptchaUrl() {
    return captchaService.getCaptchaUrl();
  }
}
