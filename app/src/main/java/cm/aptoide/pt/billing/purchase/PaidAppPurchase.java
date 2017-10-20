package cm.aptoide.pt.billing.purchase;

public class PaidAppPurchase extends Purchase {

  private final String apkPath;

  public PaidAppPurchase(String apkPath, Status status, String productId, String transactionId) {
    super(status, productId, transactionId);
    this.apkPath = apkPath;
  }

  public String getApkPath() {
    return apkPath;
  }
}