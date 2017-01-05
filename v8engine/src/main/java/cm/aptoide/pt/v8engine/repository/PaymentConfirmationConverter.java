/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.model.v3.PaymentConfirmationResponse;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;

public class PaymentConfirmationConverter {

  public PaymentConfirmation convertToPaymentConfirmation(int productId, PaymentConfirmationResponse response) {
    return new PaymentConfirmation(productId, response.getPaymentConfirmationId(),
        PaymentConfirmation.Status.valueOf(response.getPaymentStatus()));
  }

  public cm.aptoide.pt.database.realm.PaymentConfirmation convertToDatabasePaymentConfirmation(
      PaymentConfirmation paymentConfirmation) {
    return new cm.aptoide.pt.database.realm.PaymentConfirmation(paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getProductId(),
        paymentConfirmation.getStatus().name());
  }

  public PaymentConfirmation convertToPaymentConfirmation(
      cm.aptoide.pt.database.realm.PaymentConfirmation paymentConfirmation) {
    return new PaymentConfirmation(paymentConfirmation.getProductId(), paymentConfirmation.getPaymentConfirmationId(),
        PaymentConfirmation.Status.valueOf(paymentConfirmation.getStatus()));
  }
}