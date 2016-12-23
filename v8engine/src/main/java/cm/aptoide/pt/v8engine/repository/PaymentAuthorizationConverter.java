/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.model.v3.PurchaseAuthorizationResponse;
import cm.aptoide.pt.v8engine.payment.PaymentAuthorization;

public class PaymentAuthorizationConverter {

  public cm.aptoide.pt.database.realm.PaymentAuthorization convertToDatabasePaymentAuthorization(
      int paymentId, PurchaseAuthorizationResponse response) {
    return new cm.aptoide.pt.database.realm.PaymentAuthorization(paymentId, response.getUrl(),
        response.getSuccessUrl(), response.getAuthorizationStatus());
  }

  public cm.aptoide.pt.database.realm.PaymentAuthorization convertToDatabasePaymentAuthorization(
      PaymentAuthorization paymentAuthorization) {
    return new cm.aptoide.pt.database.realm.PaymentAuthorization(
        paymentAuthorization.getPaymentId(), paymentAuthorization.getUrl(),
        paymentAuthorization.getRedirectUrl(), paymentAuthorization.getStatus().name());
  }

  public PaymentAuthorization convertToPaymentAuthorization(
      cm.aptoide.pt.database.realm.PaymentAuthorization paymentAuthorization) {
    return new PaymentAuthorization(paymentAuthorization.getPaymentId(),
        paymentAuthorization.getUrl(), paymentAuthorization.getRedirectUrl(),
        PaymentAuthorization.Status.valueOf(paymentAuthorization.getStatus()));
  }

  public PaymentAuthorization convertToPaymentAuthorization(int paymentId,
      PurchaseAuthorizationResponse response) {
    return new PaymentAuthorization(paymentId, response.getUrl(), response.getSuccessUrl(),
        PaymentAuthorization.Status.valueOf(response.getStatus()));
  }
}