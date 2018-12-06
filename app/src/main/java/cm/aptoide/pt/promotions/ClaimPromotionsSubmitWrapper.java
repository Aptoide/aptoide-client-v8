package cm.aptoide.pt.promotions;

public class ClaimPromotionsSubmitWrapper {
  private String packageName;
  private String captcha;

  public ClaimPromotionsSubmitWrapper(String packageName, String captcha) {
    this.packageName = packageName;
    this.captcha = captcha;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getCaptcha() {
    return captcha;
  }
}
