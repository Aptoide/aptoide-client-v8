package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.view.BackButtonActivity;

public abstract class ProductActivity extends BackButtonActivity {

  protected static final String EXTRA_PAYMENT_ID =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.PAYMENT_ID";

  public static Bundle getBundle(int paymentId, long appId, String storeName, boolean sponsored) {
    final Bundle bundle = ProductProvider.createIntentBundle(appId, storeName, sponsored);
    bundle.putInt(EXTRA_PAYMENT_ID, paymentId);
    return bundle;
  }

  public static Bundle getBundle(int paymentId, int apiVersion, String packageName, String type,
      String sku, String developerPayload) {
    final Bundle bundle =
        ProductProvider.createIntentBundle(apiVersion, packageName, type, sku, developerPayload);
    bundle.putInt(EXTRA_PAYMENT_ID, paymentId);
    return bundle;
  }
}
