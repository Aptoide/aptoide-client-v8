/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/12/2016.
 */

package cm.aptoide.pt.v8engine.repository.sync;

import android.os.Bundle;
import android.text.TextUtils;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.payment.products.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by marcelobenites on 19/12/16.
 */

public class SyncDataConverter {

  private static final String ID = "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_ID";
  private static final String ICON = "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_ICON";
  private static final String TITLE = "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_TITLE";
  private static final String DESCRIPTION =
      "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_DESCRIPTION";

  private static final String SKU = "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_SKU";
  private static final String PACKAGE_NAME =
      "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_PACKAGE_NAME";
  private static final String DEVELOPER_PAYLOAD =
      "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_DEVELOPER_PAYLOAD";
  private static final String TYPE = "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_TYPE";
  private static final String API_VERSION =
      "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_API_VERSION";

  private static final String APP_ID = "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_APP_ID";
  private static final String STORE_NAME =
      "cm.aptoide.pt.v8engine.repository.sync.PRODUCT_STORE_NAME";

  public AptoideProduct toProduct(Bundle bundle) {
    final int id = bundle.getInt(ID, -1);
    final String icon = bundle.getString(ICON);
    final String title = bundle.getString(TITLE);
    final String description = bundle.getString(DESCRIPTION);

    final String developerPayload = bundle.getString(DEVELOPER_PAYLOAD);
    final String sku = bundle.getString(SKU);
    final String packageName = bundle.getString(PACKAGE_NAME);
    final String type = bundle.getString(TYPE);
    final int apiVersion = bundle.getInt(API_VERSION, -1);

    final long appId = bundle.getLong(APP_ID, -1);
    final String storeName = bundle.getString(STORE_NAME);

    if (id != -1 && icon != null && title != null && description != null) {
      if (developerPayload != null
          && sku != null
          && packageName != null
          && packageName != null
          && type != null
          && apiVersion != -1) {
        return new InAppBillingProduct(id, icon, title, description, apiVersion, sku,
            packageName, developerPayload, type);
      }
      if (id != -1 && storeName != null) {
        return new PaidAppProduct(id, icon, title, description, appId, storeName);
      }
    } return null;
  }

  public Bundle toBundle(AptoideProduct product) {
    final Bundle bundle = new Bundle();
    bundle.putInt(ID, product.getId());
    bundle.putString(ICON, product.getIcon());
    bundle.putString(TITLE, product.getTitle());
    bundle.putString(DESCRIPTION, product.getDescription());

    if (product instanceof InAppBillingProduct) {
      bundle.putString(DEVELOPER_PAYLOAD, ((InAppBillingProduct) product).getDeveloperPayload());
      bundle.putString(SKU, ((InAppBillingProduct) product).getSku());
      bundle.putString(PACKAGE_NAME, ((InAppBillingProduct) product).getPackageName());
      bundle.putString(TYPE, ((InAppBillingProduct) product).getType());
      bundle.putInt(API_VERSION, ((InAppBillingProduct) product).getApiVersion());
    }

    if (product instanceof PaidAppProduct) {
      bundle.putLong(APP_ID, ((PaidAppProduct) product).getAppId());
      bundle.putString(STORE_NAME, ((PaidAppProduct) product).getStoreName());
    }

    return bundle;
  }

  public String toString(List<String> list) {
    return TextUtils.join(",", list);
  }

  public List<String> toList(String listString) {
    if (listString == null) {
      return Collections.emptyList();
    }
    final String[] strings = TextUtils.split(listString, ",");
    if (strings.length == 1) {
      return Collections.singletonList(listString);
    }
    return Arrays.asList(strings);
  }
}
