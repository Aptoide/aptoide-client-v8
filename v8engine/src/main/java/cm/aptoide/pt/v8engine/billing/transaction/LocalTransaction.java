/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.transaction;

public class LocalTransaction extends Transaction {

  private final String localMetadata;

  public LocalTransaction(String productId, String payerId, Status status, int paymentMethodId,
      String localMetadata, String payload, String sellerId) {
    super(productId, payerId, status, paymentMethodId, payload, sellerId);
    this.localMetadata = localMetadata;
  }

  public String getLocalMetadata() {
    return localMetadata;
  }
}
