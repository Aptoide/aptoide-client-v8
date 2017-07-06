package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.exception.PaymentException;
import rx.Completable;

public class PaidAppPurchase implements Purchase {

  private final String apkPath;

  public PaidAppPurchase(String apkPath) {
    this.apkPath = apkPath;
  }

  public String getApkPath() {
    return apkPath;
  }

  @Override public Completable consume() {
    return Completable.error(new PaymentException("Paid app is not consumable."));
  }
}
