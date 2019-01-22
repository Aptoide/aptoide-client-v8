package cm.aptoide.pt.promotions;

public abstract class ClaimPromotionsWrapper {
  private String packageName;

  ClaimPromotionsWrapper(String packageName) {
    this.packageName = packageName;
  }

  public String getPackageName() {
    return packageName;
  }
}
