/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.authorization;

import cm.aptoide.pt.dataprovider.model.v3.PaymentAuthorizationResponse;
import cm.aptoide.pt.dataprovider.model.v3.PaymentAuthorizationsResponse;
import cm.aptoide.pt.v8engine.billing.PaymentMethodMapper;
import cm.aptoide.pt.v8engine.billing.authorization.boacompra.BoaCompraAuthorization;
import java.util.ArrayList;
import java.util.List;

public class AuthorizationFactory {

  public Authorization create(int paymentMethodId, Authorization.Status status, String payerId,
      String url, String redirectUrl) {
    switch (paymentMethodId) {
      case PaymentMethodMapper.BOA_COMPRA:
      case PaymentMethodMapper.BOA_COMPRA_GOLD:
        return new BoaCompraAuthorization(paymentMethodId, url, redirectUrl, status, payerId);
      case PaymentMethodMapper.PAYPAL:
      case PaymentMethodMapper.BRAINTREE_CREDIT_CARD:
      case PaymentMethodMapper.MOL_POINTS:
      case PaymentMethodMapper.SANDBOX:
      default:
        return new Authorization(paymentMethodId, payerId, status);
    }
  }

  public List<Authorization> map(PaymentAuthorizationsResponse response, String payerId,
      int paymentId) {

    final List<Authorization> authorizations = new ArrayList<>();
    if (response != null
        && response.isOk()
        && response.getAuthorizations() != null
        && !response.getAuthorizations()
        .isEmpty()) {

      for (PaymentAuthorizationResponse authorizationResponse : response.getAuthorizations()) {
        authorizations.add(create(authorizationResponse.getPaymentId(),
            Authorization.Status.valueOf(authorizationResponse.getAuthorizationStatus()), payerId,
            authorizationResponse.getUrl(), authorizationResponse.getSuccessUrl()));
      }
    } else {
      authorizations.add(create(paymentId, getStatus(response), payerId, "", ""));
    }

    return authorizations;
  }

  public Authorization map(PaymentAuthorizationResponse response, String payerId, int paymentId) {

    if (response != null && response.isOk()) {

      return create(response.getPaymentId(),
          Authorization.Status.valueOf(response.getAuthorizationStatus()), payerId,
          response.getUrl(), response.getSuccessUrl());
    } else {
      return create(paymentId, Authorization.Status.UNKNOWN_ERROR, payerId, "", "");
    }
  }

  private Authorization.Status getStatus(PaymentAuthorizationsResponse response) {

    if (response.getAuthorizations() != null && response.getAuthorizations()
        .isEmpty()) {
      return Authorization.Status.INACTIVE;
    }

    return Authorization.Status.UNKNOWN_ERROR;
  }
}