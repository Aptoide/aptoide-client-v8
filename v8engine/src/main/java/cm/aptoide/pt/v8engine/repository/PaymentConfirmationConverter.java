/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.model.v3.ProductPaymentResponse;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;

public class PaymentConfirmationConverter {
  public PaymentConfirmationConverter() {
  }

  public PaymentConfirmation convertToPaymentConfirmation(int productId, ProductPaymentResponse response) {
    return new PaymentConfirmation(response.getPaymentConfirmationId(), productId,
        PaymentConfirmation.Status.valueOf(response.getPaymentStatus()));
  }

  public cm.aptoide.pt.database.realm.PaymentConfirmation convertToStoredPaymentConfirmation(
      PaymentConfirmation paymentConfirmation) {
    cm.aptoide.pt.database.realm.PaymentConfirmation realmObject =
        new cm.aptoide.pt.database.realm.PaymentConfirmation(
            paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getProductId(),
            paymentConfirmation.getStatus().name());
    return realmObject;
  }

  public PaymentConfirmation convertToPaymentConfirmation(
      cm.aptoide.pt.database.realm.PaymentConfirmation paymentConfirmation) {
    return new PaymentConfirmation(paymentConfirmation.getPaymentConfirmationId(),
        paymentConfirmation.getProductId(),
        PaymentConfirmation.Status.valueOf(paymentConfirmation.getStatus()));
  }
}