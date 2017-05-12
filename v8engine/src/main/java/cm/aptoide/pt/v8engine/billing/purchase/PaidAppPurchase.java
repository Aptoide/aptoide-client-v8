package cm.aptoide.pt.v8engine.billing.purchase;

import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.exception.PaymentException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import java.io.IOException;
import rx.Completable;

public class PaidAppPurchase implements Purchase {

  private final String apkPath;

  public PaidAppPurchase(String apkPath) {
    this.apkPath = apkPath;
  }

  @Override public String getData() throws IOException {
    return apkPath;
  }

  @Override public String getSignature() {
    return null;
  }

  @Override public Completable consume() {
    return Completable.error(new PaymentException("Paid app is not consumable."));
  }
}
