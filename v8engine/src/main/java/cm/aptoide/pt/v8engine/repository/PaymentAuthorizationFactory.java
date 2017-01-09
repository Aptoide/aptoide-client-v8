/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.content.Context;
import cm.aptoide.pt.model.v3.PaymentAuthorizationsResponse;
import cm.aptoide.pt.v8engine.payment.Authorization;
import cm.aptoide.pt.v8engine.payment.authorizations.WebAuthorization;

public class PaymentAuthorizationFactory {

  private final Context context;

  public PaymentAuthorizationFactory(Context context) {
    this.context = context;
  }

  public Authorization create(int paymentId, Authorization.Status status) {
    return new WebAuthorization(context, paymentId, "", "", status);
  }

  public cm.aptoide.pt.database.realm.PaymentAuthorization convertToDatabasePaymentAuthorization(
      PaymentAuthorizationsResponse.PaymentAuthorizationResponse response) {
    return new cm.aptoide.pt.database.realm.PaymentAuthorization(response.getPaymentId(),
        response.getUrl(), response.getSuccessUrl(), response.getAuthorizationStatus());
  }

  public cm.aptoide.pt.database.realm.PaymentAuthorization convertToDatabasePaymentAuthorization(
      Authorization paymentAuthorization) {
    return new cm.aptoide.pt.database.realm.PaymentAuthorization(
        paymentAuthorization.getPaymentId(), ((WebAuthorization) paymentAuthorization).getUrl(),
        ((WebAuthorization) paymentAuthorization).getRedirectUrl(),
        paymentAuthorization.getStatus().name());
  }

  public cm.aptoide.pt.database.realm.PaymentAuthorization convertToDatabasePaymentAuthorization(int paymentId,
      Authorization.Status status) {
    return new cm.aptoide.pt.database.realm.PaymentAuthorization(paymentId, "", "", status.name());
  }

  public Authorization convertToPaymentAuthorization(
      cm.aptoide.pt.database.realm.PaymentAuthorization paymentAuthorization) {
    return new WebAuthorization(context, paymentAuthorization.getPaymentId(), paymentAuthorization.getUrl(),
        paymentAuthorization.getRedirectUrl(),
        WebAuthorization.Status.valueOf(paymentAuthorization.getStatus()));
  }

  public Authorization convertToPaymentAuthorization(
      PaymentAuthorizationsResponse.PaymentAuthorizationResponse response) {
    return new WebAuthorization(context, response.getPaymentId(), response.getUrl(),
        response.getSuccessUrl(),
        WebAuthorization.Status.valueOf(response.getAuthorizationStatus()));
  }
}