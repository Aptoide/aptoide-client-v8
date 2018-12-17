package cm.aptoide.pt.promotions;

public class ClaimPromotionsSubmitWrapper extends ClaimPromotionsWrapper {
  private String captcha;

  public ClaimPromotionsSubmitWrapper(String captcha, String packageName) {
    super(packageName);
    this.captcha = captcha;
  }

  public String getCaptcha() {
    return captcha;
  }
}
