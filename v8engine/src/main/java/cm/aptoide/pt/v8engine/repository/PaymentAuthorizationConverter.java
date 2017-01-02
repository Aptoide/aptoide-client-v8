/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.model.v3.PurchaseAuthorizationResponse;
import cm.aptoide.pt.v8engine.payment.Authorization;
import cm.aptoide.pt.v8engine.payment.authorizations.WebAuthorization;

public class PaymentAuthorizationConverter {

  public cm.aptoide.pt.database.realm.PaymentAuthorization convertToDatabasePaymentAuthorization(
      int paymentId, PurchaseAuthorizationResponse response) {
    return new cm.aptoide.pt.database.realm.PaymentAuthorization(paymentId, response.getUrl(),
        response.getSuccessUrl(), response.getAuthorizationStatus());
  }

  public cm.aptoide.pt.database.realm.PaymentAuthorization convertToDatabasePaymentAuthorization(
      Authorization paymentAuthorization) {
    return new cm.aptoide.pt.database.realm.PaymentAuthorization(
        paymentAuthorization.getPaymentId(), ((WebAuthorization) paymentAuthorization).getUrl(),
        ((WebAuthorization) paymentAuthorization).getRedirectUrl(),
        paymentAuthorization.getStatus().name());
  }

  public Authorization convertToPaymentAuthorization(
      cm.aptoide.pt.database.realm.PaymentAuthorization paymentAuthorization) {
    return new WebAuthorization(paymentAuthorization.getPaymentId(), paymentAuthorization.getUrl(),
        paymentAuthorization.getRedirectUrl(),
        WebAuthorization.Status.valueOf(paymentAuthorization.getStatus()));
  }

  public Authorization convertToPaymentAuthorization(int paymentId,
      PurchaseAuthorizationResponse response) {
    return new WebAuthorization(paymentId, response.getUrl(), response.getSuccessUrl(),
        WebAuthorization.Status.valueOf(response.getStatus()));
  }
}