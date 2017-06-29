/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/12/2016.
 */

package cm.aptoide.pt.v8engine.sync.adapter;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Price;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;

public class ProductBundleMapper {

  private static final String ID = "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_ID";
  private static final String ICON = "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_ICON";
  private static final String TITLE = "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_TITLE";
  private static final String DESCRIPTION =
      "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_DESCRIPTION";
  private static final String AMOUNT = "cm.aptoide.pt.v8engine.repository.sync.PRICE_AMOUNT";
  private static final String PACKAGE_VERSION_CODE =
      "cm.aptoide.pt.v8engine.repository.sync.PACKAGE_VERSION_CODE";

  private static final String SKU = "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_SKU";
  private static final String PACKAGE_NAME =
      "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_PACKAGE_NAME";
  private static final String APPLICATION_NAME =
      "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_APPLICATION_NAME";
  private static final String DEVELOPER_PAYLOAD =
      "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_DEVELOPER_PAYLOAD";
  private static final String TYPE = "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_TYPE";
  private static final String API_VERSION =
      "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_API_VERSION";

  private static final String APP_ID = "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_APP_ID";
  private static final String STORE_NAME =
      "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_STORE_NAME";
  private static final String SPONSORED =
      "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_SPONSORED";
  private static final String TAX_RATE = "cm.aptoide.pt.v8engine.repository.sync.PRICE_TAX_RATE";
  private static final String CURRENCY = "cm.aptoide.pt.v8engine.repository.sync.PRICE_CURRENCY";
  private static final String CURRENCY_SYMBOL =
      "cm.aptoide.pt.v8engine.repository.sync.PRICE_CURRENCY_SYMBOL";

  public Product mapToProduct(Bundle bundle) {
    final int id = bundle.getInt(ID, -1);
    final String icon = bundle.getString(ICON);
    final String title = bundle.getString(TITLE);
    final String description = bundle.getString(DESCRIPTION);
    final double amount = bundle.getDouble(AMOUNT, -1);
    final double taxRate = bundle.getDouble(TAX_RATE, -1);
    final String currency = bundle.getString(CURRENCY);
    final String currencySymbol = bundle.getString(CURRENCY_SYMBOL);
    final int packageVersionCode = bundle.getInt(PACKAGE_VERSION_CODE, -1);

    final String developerPayload = bundle.getString(DEVELOPER_PAYLOAD);
    final String sku = bundle.getString(SKU);
    final String packageName = bundle.getString(PACKAGE_NAME);
    final String applicationName = bundle.getString(APPLICATION_NAME);
    final String type = bundle.getString(TYPE);
    final int apiVersion = bundle.getInt(API_VERSION, -1);

    final long appId = bundle.getLong(APP_ID, -1);
    final String storeName = bundle.getString(STORE_NAME);
    final boolean sponsored = bundle.getBoolean(SPONSORED);

    if (id != -1
        && icon != null
        && title != null
        && description != null
        && amount != -1
        && packageVersionCode != -1
        && taxRate != -1
        && currency != null
        && currencySymbol != null) {

      final Price price = new Price(amount, currency, currencySymbol, taxRate);

      if (developerPayload != null
          && sku != null
          && packageName != null
          && applicationName != null
          && type != null
          && apiVersion != -1) {
        return new InAppProduct(id, icon, title, description, apiVersion, sku, packageName,
            developerPayload, type, price, packageVersionCode, applicationName);
      }
      if (id != -1 && storeName != null) {
        return new PaidAppProduct(id, icon, title, description, appId, storeName, price, sponsored,
            packageVersionCode);
      }
    }
    return null;
  }

  public Bundle mapToBundle(Product product) {
    final Bundle bundle = new Bundle();
    bundle.putInt(ID, product.getId());
    bundle.putString(ICON, product.getIcon());
    bundle.putString(TITLE, product.getTitle());
    bundle.putString(DESCRIPTION, product.getDescription());
    bundle.putDouble(AMOUNT, product.getPrice()
        .getAmount());
    bundle.putDouble(TAX_RATE, product.getPrice()
        .getTaxRate());
    bundle.putString(CURRENCY, product.getPrice()
        .getCurrency());
    bundle.putString(CURRENCY_SYMBOL, product.getPrice()
        .getCurrencySymbol());

    if (product instanceof InAppProduct) {
      bundle.putString(DEVELOPER_PAYLOAD, ((InAppProduct) product).getDeveloperPayload());
      bundle.putString(SKU, ((InAppProduct) product).getSku());
      bundle.putString(PACKAGE_NAME, ((InAppProduct) product).getPackageName());
      bundle.putString(APPLICATION_NAME, ((InAppProduct) product).getApplicationName());
      bundle.putString(TYPE, ((InAppProduct) product).getType());
      bundle.putInt(API_VERSION, ((InAppProduct) product).getApiVersion());
      bundle.putInt(PACKAGE_VERSION_CODE, ((InAppProduct) product).getPackageVersionCode());
    }

    if (product instanceof PaidAppProduct) {
      bundle.putLong(APP_ID, ((PaidAppProduct) product).getAppId());
      bundle.putString(STORE_NAME, ((PaidAppProduct) product).getStoreName());
      bundle.putBoolean(SPONSORED, ((PaidAppProduct) product).isSponsored());
      bundle.putInt(PACKAGE_VERSION_CODE, ((PaidAppProduct) product).getPackageVersionCode());
    }

    return bundle;
  }
}