/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.model.v3.PaymentAuthorizationsResponse;
import cm.aptoide.pt.v8engine.billing.Authorization;
import cm.aptoide.pt.v8engine.billing.authorizations.WebAuthorization;

public class PaymentAuthorizationFactory {

  public Authorization create(int paymentId, Authorization.Status status, String payerId) {
    return new WebAuthorization(paymentId, "", "", status, payerId);
  }

  public cm.aptoide.pt.database.realm.PaymentAuthorization convertToDatabasePaymentAuthorization(
      PaymentAuthorizationsResponse.PaymentAuthorizationResponse response, String payerId) {
    return new cm.aptoide.pt.database.realm.PaymentAuthorization(response.getPaymentId(),
        response.getUrl(), response.getSuccessUrl(), response.getAuthorizationStatus(), payerId);
  }

  public cm.aptoide.pt.database.realm.PaymentAuthorization convertToDatabasePaymentAuthorization(
      Authorization paymentAuthorization) {
    return new cm.aptoide.pt.database.realm.PaymentAuthorization(
        paymentAuthorization.getPaymentId(), ((WebAuthorization) paymentAuthorization).getUrl(),
        ((WebAuthorization) paymentAuthorization).getRedirectUrl(), paymentAuthorization.getStatus()
        .name(), paymentAuthorization.getPayerId());
  }

  public cm.aptoide.pt.database.realm.PaymentAuthorization convertToDatabasePaymentAuthorization(
      int paymentId, Authorization.Status status, String payerId) {
    return new cm.aptoide.pt.database.realm.PaymentAuthorization(paymentId, "", "", status.name(),
        payerId);
  }

  public Authorization convertToPaymentAuthorization(
      cm.aptoide.pt.database.realm.PaymentAuthorization paymentAuthorization) {
    return new WebAuthorization(paymentAuthorization.getPaymentId(), paymentAuthorization.getUrl(),
        paymentAuthorization.getRedirectUrl(),
        WebAuthorization.Status.valueOf(paymentAuthorization.getStatus()),
        paymentAuthorization.getPayerId());
  }

  public Authorization convertToPaymentAuthorization(
      PaymentAuthorizationsResponse.PaymentAuthorizationResponse response, String payerId) {
    return new WebAuthorization(response.getPaymentId(), response.getUrl(),
        response.getSuccessUrl(),
        WebAuthorization.Status.valueOf(response.getAuthorizationStatus()), payerId);
  }
}