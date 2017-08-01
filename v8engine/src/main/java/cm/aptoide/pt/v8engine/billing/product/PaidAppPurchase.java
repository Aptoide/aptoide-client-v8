package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.v8engine.billing.Purchase;

public class PaidAppPurchase implements Purchase {

  private final String apkPath;
  private final boolean completed;

  public PaidAppPurchase(String apkPath, boolean completed) {
    this.apkPath = apkPath;
    this.completed = completed;
  }

  public String getApkPath() {
    return apkPath;
  }

  @Override public boolean isCompleted() {
    return completed;
  }
}