/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.Price;

public class PayPalAuthorization extends Authorization {

  private final String metadata;
  private final Price price;
  private final String description;

  public PayPalAuthorization(long id, String customerId, Status status, long transactionId,
      String metadata, Price price, String description) {
    super(id, customerId, status, transactionId);
    this.metadata = metadata;
    this.price = price;
    this.description = description;
  }

  public String getMetadata() {
    return metadata;
  }

  public Price getPrice() {
    return price;
  }

  public String getDescription() {
    return description;
  }
}
