/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.methods.paypal;

import cm.aptoide.pt.v8engine.billing.Transaction;

public class PayPalTransaction extends Transaction {

  private final String payPalConfirmationId;

  public PayPalTransaction(int productId, String payerId, String payPalConfirmationId,
      Status status, int paymentMethodId) {
    super(productId, payerId, status, paymentMethodId);
    this.payPalConfirmationId = payPalConfirmationId;
  }

  public String getPayPalConfirmationId() {
    return payPalConfirmationId;
  }
}
