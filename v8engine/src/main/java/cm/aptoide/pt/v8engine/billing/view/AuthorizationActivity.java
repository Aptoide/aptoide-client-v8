package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.view.BackButtonActivity;

public abstract class AuthorizationActivity extends BackButtonActivity {

  protected static final String EXTRA_APP_ID =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.APP_ID";
  protected static final String EXTRA_STORE_NAME =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.STORE_NAME";
  protected static final String EXTRA_SPONSORED =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.SPONSORED";
  protected static final String EXTRA_API_VERSION =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.API_VERSION";
  protected static final String EXTRA_PACKAGE_NAME =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.PACKAGE_NAME";
  protected static final String EXTRA_SKU = "cm.aptoide.pt.v8engine.view.payment.intent.extra.SKU";
  protected static final String EXTRA_TYPE = "cm.aptoide.pt.v8engine.view.payment.intent.extra.TYPE";
  protected static final String EXTRA_DEVELOPER_PAYLOAD =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.DEVELOPER_PAYLOAD";
  protected static final String EXTRA_PAYMENT_ID =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.PAYMENT_ID";

  public static Bundle getBundle(int paymentId, long appId, String storeName, boolean sponsored) {
    final Bundle bundle = new Bundle();
    bundle.putLong(EXTRA_APP_ID, appId);
    bundle.putString(EXTRA_STORE_NAME, storeName);
    bundle.putBoolean(EXTRA_SPONSORED, sponsored);
    bundle.putInt(EXTRA_PAYMENT_ID, paymentId);
    return bundle;
  }

  public static Bundle getBundle(int paymentId, int apiVersion, String packageName, String type,
      String sku, String developerPayload) {
    final Bundle bundle = new Bundle();
    bundle.putInt(EXTRA_PAYMENT_ID, paymentId);
    bundle.putInt(EXTRA_API_VERSION, apiVersion);
    bundle.putString(EXTRA_PACKAGE_NAME, packageName);
    bundle.putString(EXTRA_TYPE, type);
    bundle.putString(EXTRA_SKU, sku);
    bundle.putString(EXTRA_DEVELOPER_PAYLOAD, developerPayload);
    return bundle;
  }
}
