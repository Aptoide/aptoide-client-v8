/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.billing.product;

import cm.aptoide.pt.billing.Price;

public class InAppProduct extends SimpleProduct {

  private final String sku;
  private final String packageName;

  public InAppProduct(String id, String sku, String icon, String title, String description,
      String packageName, Price price, int packageVersionCode) {
    super(id, icon, title, description, price, packageVersionCode);
    this.sku = sku;
    this.packageName = packageName;
  }

  public String getSku() {
    return sku;
  }

  public String getPackageName() {
    return packageName;
  }
}