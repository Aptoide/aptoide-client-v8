/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.billing.product;

import cm.aptoide.pt.billing.Price;

public class PaidAppProduct extends AbstractProduct {

  private final String storeName;
  private final long appId;

  public PaidAppProduct(long id, String storeName, String icon, String title, String description,
      long appId, Price price, int packageVersionCode) {
    super(id, icon, title, description, price, packageVersionCode);
    this.storeName = storeName;
    this.appId = appId;
  }

  public long getAppId() {
    return appId;
  }

  public String getStoreName() {
    return storeName;
  }
}
