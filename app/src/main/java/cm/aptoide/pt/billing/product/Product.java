/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 30/08/2016.
 */

package cm.aptoide.pt.billing.product;

import cm.aptoide.pt.billing.Price;

public class Product {

  private final String id;
  private final String icon;
  private final String title;
  private final String description;
  private final Price price;
  private final int packageVersionCode;

  public Product(String id, String icon, String title, String description, Price price,
      int packageVersionCode) {
    this.id = id;
    this.icon = icon;
    this.title = title;
    this.description = description;
    this.price = price;
    this.packageVersionCode = packageVersionCode;
  }

  public String getProductId() {
    return id;
  }

  public String getIcon() {
    return icon;
  }

  public String getTitle() {
    return title;
  }

  public Price getPrice() {
    return price;
  }

  public String getDescription() {
    return description;
  }

  public int getPackageVersionCode() {
    return packageVersionCode;
  }
}