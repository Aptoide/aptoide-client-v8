/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.Price;

public class PayPalAuthorization extends MetadataAuthorization {

  private final Price price;
  private final String description;

  public PayPalAuthorization(String id, String customerId, Status status, String transactionId,
      String metadata, Price price, String description) {
    super(id, customerId, status, transactionId, metadata);
    this.price = price;
    this.description = description;
  }

  public Price getPrice() {
    return price;
  }

  public String getDescription() {
    return description;
  }
}
