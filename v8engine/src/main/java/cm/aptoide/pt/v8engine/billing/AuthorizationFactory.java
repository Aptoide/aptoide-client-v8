/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.dataprovider.model.v3.PaymentAuthorizationsResponse;
import cm.aptoide.pt.v8engine.billing.Authorization;
import cm.aptoide.pt.v8engine.billing.methods.boacompra.BoaCompraAuthorization;
import java.util.ArrayList;
import java.util.List;

public class AuthorizationFactory {

  public Authorization create(int paymentId, Authorization.Status status, String payerId) {
    return new BoaCompraAuthorization(paymentId, "", "", status, payerId);
  }

  public cm.aptoide.pt.database.realm.PaymentAuthorization convertToDatabasePaymentAuthorization(
      Authorization authorization) {
    return new cm.aptoide.pt.database.realm.PaymentAuthorization(authorization.getPaymentId(),
        ((BoaCompraAuthorization) authorization).getUrl(),
        ((BoaCompraAuthorization) authorization).getRedirectUrl(), authorization.getStatus()
        .name(), authorization.getPayerId());
  }

  public Authorization convertToPaymentAuthorization(
      cm.aptoide.pt.database.realm.PaymentAuthorization paymentAuthorization) {
    return new BoaCompraAuthorization(paymentAuthorization.getPaymentId(),
        paymentAuthorization.getUrl(), paymentAuthorization.getRedirectUrl(),
        Authorization.Status.valueOf(paymentAuthorization.getStatus()),
        paymentAuthorization.getPayerId());
  }

  public List<Authorization> convertToPaymentAuthorizations(PaymentAuthorizationsResponse response,
      String payerId, int paymentId) {

    final List<Authorization> authorizations = new ArrayList<>();
    if (response != null
        && response.isOk()
        && response.getAuthorizations() != null
        && !response.getAuthorizations()
        .isEmpty()) {

      for (PaymentAuthorizationsResponse.PaymentAuthorizationResponse authorizationResponse : response.getAuthorizations()) {
        authorizations.add(convertToPaymentAuthorization(authorizationResponse, payerId));
      }
    } else {
      authorizations.add(create(paymentId, getStatus(response), payerId));
    }

    return authorizations;
  }

  private Authorization.Status getStatus(PaymentAuthorizationsResponse response) {

    if (response.getAuthorizations() != null && response.getAuthorizations()
        .isEmpty()) {
      return Authorization.Status.INACTIVE;
    }

    return Authorization.Status.UNKNOWN_ERROR;
  }

  private Authorization convertToPaymentAuthorization(
      PaymentAuthorizationsResponse.PaymentAuthorizationResponse response, String payerId) {

    return new BoaCompraAuthorization(response.getPaymentId(), response.getUrl(),
        response.getSuccessUrl(), Authorization.Status.valueOf(response.getAuthorizationStatus()),
        payerId);
  }
}