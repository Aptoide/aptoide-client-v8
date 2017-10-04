/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.billing.transaction;

public class LocalTransaction extends Transaction {

  private final String localMetadata;

  public LocalTransaction(String productId, String customerId, Status status, int paymentMethodId,
      String localMetadata, String payload, String sellerId) {
    super(productId, customerId, status, paymentMethodId, payload, sellerId);
    this.localMetadata = localMetadata;
  }

  public String getLocalMetadata() {
    return localMetadata;
  }
}
