/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.model.v3.PaymentAuthorizationsResponse;
import cm.aptoide.pt.v8engine.billing.Authorization;
import cm.aptoide.pt.v8engine.billing.services.PayPalAuthorization;
import cm.aptoide.pt.v8engine.billing.services.WebAuthorization;
import java.util.ArrayList;
import java.util.List;

public class AuthorizationFactory {

  public static final String WEB = "web";
  public static final String PAYPAL = "paypal_sdk";

  public Authorization create(int paymentId, Authorization.Status status, String payerId,
      String type) {

    if (WEB.equals(type)) {
      return new WebAuthorization(paymentId, "", "", status, payerId);
    }

    if (PAYPAL.equals(type)) {
      return new PayPalAuthorization(paymentId, status, payerId);
    }

    throw new IllegalArgumentException("Invalid authorization type");
  }

  public cm.aptoide.pt.database.realm.PaymentAuthorization convertToDatabasePaymentAuthorization(
      Authorization authorization) {

    String type = getType(authorization);

    if (authorization instanceof WebAuthorization) {
      return new cm.aptoide.pt.database.realm.PaymentAuthorization(authorization.getPaymentId(),
          ((WebAuthorization) authorization).getUrl(),
          ((WebAuthorization) authorization).getRedirectUrl(), authorization.getStatus()
          .name(), authorization.getPayerId(), type);
    }

    if (authorization instanceof PayPalAuthorization) {
      return new cm.aptoide.pt.database.realm.PaymentAuthorization(authorization.getPaymentId(), "",
          "", authorization.getStatus()
          .name(), authorization.getPayerId(), type);
    }

    throw new IllegalArgumentException("Invalid authorization type");
  }

  public Authorization convertToPaymentAuthorization(
      cm.aptoide.pt.database.realm.PaymentAuthorization paymentAuthorization) {

    if (WEB.equals(paymentAuthorization.getType())) {
      return new WebAuthorization(paymentAuthorization.getPaymentId(),
          paymentAuthorization.getUrl(), paymentAuthorization.getRedirectUrl(),
          WebAuthorization.Status.valueOf(paymentAuthorization.getStatus()),
          paymentAuthorization.getPayerId());
    }

    if (PAYPAL.equals(paymentAuthorization.getType())) {
      return new PayPalAuthorization(paymentAuthorization.getPaymentId(),
          Authorization.Status.valueOf(paymentAuthorization.getStatus()),
          paymentAuthorization.getPayerId());
    }

    throw new IllegalArgumentException("Invalid authorization type");
  }

  public List<Authorization> convertToPaymentAuthorizations(PaymentAuthorizationsResponse response,
      String payerId, int paymentId, String authorizationType) {

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
      authorizations.add(create(paymentId, getStatus(response), payerId, authorizationType));
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

    if (WEB.equals(response.getType())) {
      return new WebAuthorization(response.getPaymentId(), response.getUrl(),
          response.getSuccessUrl(), Authorization.Status.valueOf(response.getAuthorizationStatus()),
          payerId);
    }

    if (PAYPAL.equals(response.getType())) {
      return new PayPalAuthorization(response.getPaymentId(),
          Authorization.Status.valueOf(response.getAuthorizationStatus()), payerId);
    }

    throw new IllegalArgumentException("Invalid authorization type");
  }

  private String getType(Authorization authorization) {
    if (authorization instanceof WebAuthorization) {
      return WEB;
    }

    if (authorization instanceof PayPalAuthorization) {
      return PAYPAL;
    }

    throw new IllegalArgumentException("Invalid authorization type");
  }
}