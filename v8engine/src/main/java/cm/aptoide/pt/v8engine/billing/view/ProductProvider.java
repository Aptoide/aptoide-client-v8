package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.Product;
import rx.Single;

public class ProductProvider {

  private static final String EXTRA_APP_ID =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.APP_ID";
  private static final String EXTRA_STORE_NAME =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.STORE_NAME";
  private static final String EXTRA_SPONSORED =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.SPONSORED";
  private static final String EXTRA_API_VERSION =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.API_VERSION";
  private static final String EXTRA_PACKAGE_NAME =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.PACKAGE_NAME";
  private static final String EXTRA_SKU = "cm.aptoide.pt.v8engine.view.payment.intent.extra.SKU";
  private static final String EXTRA_TYPE = "cm.aptoide.pt.v8engine.view.payment.intent.extra.TYPE";
  private static final String EXTRA_DEVELOPER_PAYLOAD =
      "cm.aptoide.pt.v8engine.view.payment.intent.extra.DEVELOPER_PAYLOAD";

  private final Billing billing;

  private final long appId;
  private final String storeName;
  private final boolean sponsored;

  private final int apiVersion;
  private final String type;
  private final String sku;
  private final String packageName;
  private final String developerPayload;

  private ProductProvider(Billing billing, long appId, String storeName, boolean sponsored,
      int apiVersion, String type, String sku, String packageName, String developerPayload) {
    this.billing = billing;
    this.appId = appId;
    this.storeName = storeName;
    this.sponsored = sponsored;
    this.apiVersion = apiVersion;
    this.type = type;
    this.sku = sku;
    this.packageName = packageName;
    this.developerPayload = developerPayload;
  }

  public static ProductProvider fromBundle(Billing billing, Bundle bundle) {
    return new ProductProvider(billing, bundle.getLong(EXTRA_APP_ID, -1),
        bundle.getString(EXTRA_STORE_NAME), bundle.getBoolean(EXTRA_SPONSORED, false),
        bundle.getInt(EXTRA_API_VERSION, -1), bundle.getString(EXTRA_TYPE),
        bundle.getString(EXTRA_SKU), bundle.getString(EXTRA_PACKAGE_NAME),
        bundle.getString(EXTRA_DEVELOPER_PAYLOAD));
  }

  public static Bundle createBundle(long appId, String storeName, boolean sponsored) {
    final Bundle bundle = new Bundle();
    bundle.putLong(ProductProvider.EXTRA_APP_ID, appId);
    bundle.putString(ProductProvider.EXTRA_STORE_NAME, storeName);
    bundle.putBoolean(ProductProvider.EXTRA_SPONSORED, sponsored);
    return bundle;
  }

  public static Bundle createBundle(int apiVersion, String packageName, String type, String sku,
      String developerPayload) {
    final Bundle bundle = new Bundle();
    bundle.putInt(ProductProvider.EXTRA_API_VERSION, apiVersion);
    bundle.putString(ProductProvider.EXTRA_PACKAGE_NAME, packageName);
    bundle.putString(ProductProvider.EXTRA_TYPE, type);
    bundle.putString(ProductProvider.EXTRA_SKU, sku);
    bundle.putString(ProductProvider.EXTRA_DEVELOPER_PAYLOAD, developerPayload);
    return bundle;
  }

  public Single<Product> getProduct() {

    if (storeName != null) {
      return billing.getProduct(appId, storeName, sponsored);
    }

    if (sku != null) {
      return billing.getProduct(packageName, apiVersion, type, sku, developerPayload);
    }

    return Single.error(new IllegalArgumentException("Invalid product information."));
  }
}
