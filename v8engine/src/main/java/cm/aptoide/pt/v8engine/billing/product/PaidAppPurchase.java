package cm.aptoide.pt.v8engine.billing.product;

public class PaidAppPurchase extends SimplePurchase {

  private final String apkPath;

  public PaidAppPurchase(String apkPath, Status status, String productId) {
    super(status, productId);
    this.apkPath = apkPath;
  }

  public String getApkPath() {
    return apkPath;
  }
}