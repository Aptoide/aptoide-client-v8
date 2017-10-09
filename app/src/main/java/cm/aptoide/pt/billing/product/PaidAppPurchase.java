package cm.aptoide.pt.billing.product;

public class PaidAppPurchase extends SimplePurchase {

  private final String apkPath;

  public PaidAppPurchase(String apkPath, Status status, long productId) {
    super(status, productId);
    this.apkPath = apkPath;
  }

  public String getApkPath() {
    return apkPath;
  }
}