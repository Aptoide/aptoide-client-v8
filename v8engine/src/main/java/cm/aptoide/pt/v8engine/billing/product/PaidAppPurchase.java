package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.v8engine.billing.Purchase;

public class PaidAppPurchase implements Purchase {

  private final String apkPath;

  public PaidAppPurchase(String apkPath) {
    this.apkPath = apkPath;
  }

  public String getApkPath() {
    return apkPath;
  }
}