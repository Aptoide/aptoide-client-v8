/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.transaction;

import cm.aptoide.pt.v8engine.billing.transaction.Transaction;

public class LocalTransaction extends Transaction {

  private final String localMetadata;

  public LocalTransaction(int productId, String payerId, Status status, int paymentMethodId,
      String localMetadata) {
    super(productId, payerId, status, paymentMethodId);
    this.localMetadata = localMetadata;
  }

  public String getLocalMetadata() {
    return localMetadata;
  }
}
