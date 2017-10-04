/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.PaymentMethodMapper;
import cm.aptoide.pt.billing.authorization.boacompra.BoaCompraAuthorization;
import cm.aptoide.pt.dataprovider.model.v3.PaymentAuthorizationResponse;
import cm.aptoide.pt.dataprovider.model.v3.PaymentAuthorizationsResponse;
import java.util.ArrayList;
import java.util.List;

public class AuthorizationFactory {

  public Authorization create(int paymentMethodId, Authorization.Status status, String customerId,
      String url, String redirectUrl) {
    switch (paymentMethodId) {
      case PaymentMethodMapper.BOA_COMPRA:
      case PaymentMethodMapper.BOA_COMPRA_GOLD:
        return new BoaCompraAuthorization(paymentMethodId, url, redirectUrl, status, customerId);
      case PaymentMethodMapper.PAYPAL:
      case PaymentMethodMapper.BRAINTREE_CREDIT_CARD:
      case PaymentMethodMapper.MOL_POINTS:
      case PaymentMethodMapper.SANDBOX:
      default:
        return new Authorization(paymentMethodId, customerId, status);
    }
  }

  public List<Authorization> map(PaymentAuthorizationsResponse response, String customerId,
      int paymentId) {

    final List<Authorization> authorizations = new ArrayList<>();
    if (response != null
        && response.isOk()
        && response.getAuthorizations() != null
        && !response.getAuthorizations()
        .isEmpty()) {

      for (PaymentAuthorizationResponse authorizationResponse : response.getAuthorizations()) {
        authorizations.add(create(authorizationResponse.getPaymentId(),
            Authorization.Status.valueOf(authorizationResponse.getAuthorizationStatus()), customerId,
            authorizationResponse.getUrl(), authorizationResponse.getSuccessUrl()));
      }
    } else {
      authorizations.add(create(paymentId, getStatus(response), customerId, "", ""));
    }

    return authorizations;
  }

  public Authorization map(PaymentAuthorizationResponse response, String customerId, int paymentId) {

    if (response != null && response.isOk()) {

      return create(response.getPaymentId(),
          Authorization.Status.valueOf(response.getAuthorizationStatus()), customerId,
          response.getUrl(), response.getSuccessUrl());
    } else {
      return create(paymentId, Authorization.Status.UNKNOWN_ERROR, customerId, "", "");
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