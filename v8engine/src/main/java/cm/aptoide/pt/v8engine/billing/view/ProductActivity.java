package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.view.BackButtonActivity;
import cm.aptoide.pt.v8engine.view.BaseActivity;

public abstract class ProductActivity extends BaseActivity {

  protected static final String EXTRA_PAYMENT_ID =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.PAYMENT_ID";

  public static Bundle getBundle(int paymentId, long appId, String storeName, boolean sponsored) {
    final Bundle bundle = ProductProvider.createBundle(appId, storeName, sponsored);
    bundle.putInt(EXTRA_PAYMENT_ID, paymentId);
    return bundle;
  }

  public static Bundle getBundle(int paymentId, int apiVersion, String packageName, String type,
      String sku, String developerPayload) {
    final Bundle bundle =
        ProductProvider.createBundle(apiVersion, packageName, type, sku, developerPayload);
    bundle.putInt(EXTRA_PAYMENT_ID, paymentId);
    return bundle;
  }
}
