/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.v8engine.billing.Price;

public class InAppProduct extends AbstractProduct {

  private final int apiVersion;
  private final String sku;
  private final String packageName;
  private final String applicationName;

  public InAppProduct(String id, int internalId, String icon, String title, String description, int apiVersion,
      String sku, String packageName, Price price, int packageVersionCode, String applicationName) {
    super(id, internalId, icon, title, description, price, packageVersionCode);
    this.apiVersion = apiVersion;
    this.sku = sku;
    this.packageName = packageName;
    this.applicationName = applicationName;
  }

  public int getApiVersion() {
    return apiVersion;
  }

  public String getSku() {
    return sku;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getApplicationName() {
    return applicationName;
  }
}