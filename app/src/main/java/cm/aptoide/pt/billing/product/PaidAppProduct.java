/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.billing.product;

import cm.aptoide.pt.billing.Price;

public class PaidAppProduct extends AbstractProduct {

  private final long appId;
  private final String storeName;
  private final boolean sponsored;

  public PaidAppProduct(int id, String icon, String title, String description, long appId,
      String storeName, Price price, boolean sponsored, int packageVersionCode) {
    super(id, icon, title, description, price, packageVersionCode);
    this.appId = appId;
    this.storeName = storeName;
    this.sponsored = sponsored;
  }

  public long getAppId() {
    return appId;
  }

  public String getStoreName() {
    return storeName;
  }

  public boolean isSponsored() {
    return sponsored;
  }
}
