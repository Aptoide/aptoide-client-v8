/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 30/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.v8engine.billing.Price;
import cm.aptoide.pt.v8engine.billing.Product;

public abstract class AbstractProduct implements Product {

  private final int packageVersionCode;
  private final String id;
  private final int internalId;
  private final String icon;
  private final String title;
  private final String description;
  private final Price price;

  public AbstractProduct(String id, int internalId, String icon, String title, String description,
      Price price, int packageVersionCode) {
    this.id = id;
    this.internalId = internalId;
    this.icon = icon;
    this.title = title;
    this.description = description;
    this.price = price;
    this.packageVersionCode = packageVersionCode;
  }

  @Override public String getId() {
    return id;
  }

  @Override public String getIcon() {
    return icon;
  }

  @Override public String getTitle() {
    return title;
  }

  @Override public Price getPrice() {
    return price;
  }

  @Override public String getDescription() {
    return description;
  }

  public int getPackageVersionCode() {
    return packageVersionCode;
  }

  public int getInternalId() {
    return internalId;
  }
}