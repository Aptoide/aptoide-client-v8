/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 30/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.v8engine.billing.Price;
import cm.aptoide.pt.v8engine.billing.Product;

public abstract class AbstractProduct implements Product {

  private final int packageVersionCode;
  private final int id;
  private final String icon;
  private final String title;
  private final String description;
  private final Price price;

  public AbstractProduct(int id, String icon, String title, String description, Price price,
      int packageVersionCode) {
    this.id = id;
    this.icon = icon;
    this.title = title;
    this.description = description;
    this.price = price;
    this.packageVersionCode = packageVersionCode;
  }

  @Override public int getId() {
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
}