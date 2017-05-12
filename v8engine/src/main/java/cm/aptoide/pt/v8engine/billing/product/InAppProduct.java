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
  private final String developerPayload;
  private final String type;

  public InAppProduct(int id, String icon, String title, String description, int apiVersion,
      String sku, String packageName, String developerPayload, String type, Price price) {
    super(id, icon, title, description, price);
    this.apiVersion = apiVersion;
    this.sku = sku;
    this.packageName = packageName;
    this.developerPayload = developerPayload;
    this.type = type;
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

  public String getDeveloperPayload() {
    return developerPayload;
  }

  public String getType() {
    return type;
  }
}