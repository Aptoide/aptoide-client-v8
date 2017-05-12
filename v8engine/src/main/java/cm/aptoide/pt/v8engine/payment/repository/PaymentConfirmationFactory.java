/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.payment.repository;

import cm.aptoide.pt.model.v3.PaymentConfirmationResponse;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;

public class PaymentConfirmationFactory {

  public PaymentConfirmation create(int productId, String paymentConfirmationId,
      PaymentConfirmation.Status status, String payerId) {
    return new PaymentConfirmation(productId, payerId, paymentConfirmationId, status);
  }

  public PaymentConfirmation convertToPaymentConfirmation(int productId,
      PaymentConfirmationResponse response, String payerId) {
    return new PaymentConfirmation(productId, payerId, response.getPaymentConfirmationId(),
        PaymentConfirmation.Status.valueOf(response.getPaymentStatus()));
  }

  public cm.aptoide.pt.database.realm.PaymentConfirmation convertToDatabasePaymentConfirmation(
      PaymentConfirmation paymentConfirmation) {
    return new cm.aptoide.pt.database.realm.PaymentConfirmation(
        paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getProductId(),
        paymentConfirmation.getStatus()
            .name(), paymentConfirmation.getPayerId());
  }

  public PaymentConfirmation convertToPaymentConfirmation(
      cm.aptoide.pt.database.realm.PaymentConfirmation paymentConfirmation) {
    return new PaymentConfirmation(paymentConfirmation.getProductId(),
        paymentConfirmation.getPayerId(), paymentConfirmation.getPaymentConfirmationId(),
        PaymentConfirmation.Status.valueOf(paymentConfirmation.getStatus()));
  }
}