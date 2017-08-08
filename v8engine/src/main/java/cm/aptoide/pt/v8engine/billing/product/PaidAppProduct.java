/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.product;

import cm.aptoide.pt.v8engine.billing.Price;

public class PaidAppProduct extends AbstractProduct {

  private final long appId;

  public PaidAppProduct(String id, int internalId, String icon, String title, String description,
      long appId, Price price, int packageVersionCode) {
    super(id, internalId, icon, title, description, price, packageVersionCode);
    this.appId = appId;
  }

  public long getAppId() {
    return appId;
  }
}
