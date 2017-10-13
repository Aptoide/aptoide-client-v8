/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.Price;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import java.util.List;

public class AuthorizationMapperV3 {

  private final AuthorizationFactory authorizationFactory;

  public AuthorizationMapperV3(AuthorizationFactory authorizationFactory) {
    this.authorizationFactory = authorizationFactory;
  }

  public Authorization map(String authorizationId, String customerId, String transactionId,
      TransactionResponse transactionResponse, PaidApp paidApp) {

    final PaymentServiceResponse paymentServiceResponse = paidApp.getPayment()
        .getPaymentServices()
        .get(0);

    final String description = paidApp.getApp()
        .getName();

    final Price price =
        new Price(paymentServiceResponse.getPrice(), paymentServiceResponse.getCurrency(),
            paymentServiceResponse.getSign());

    if (transactionResponse.hasErrors()) {
      return getErrorAuthorization(transactionResponse.getErrors(), authorizationId, transactionId,
          customerId, AuthorizationFactory.PAYPAL_SDK, description, price);
    }

    Authorization.Status status;
    switch (transactionResponse.getTransactionStatus()) {
      case "COMPLETED":
        status = Authorization.Status.REDEEMED;
        break;
      case "PENDING_USER_AUTHORIZATION":
      case "CREATED":
        status = Authorization.Status.PENDING;
        break;
      case "PROCESSING":
      case "PENDING":
        status = Authorization.Status.PROCESSING;
        break;
      case "FAILED":
      case "CANCELED":
      default:
        status = Authorization.Status.FAILED;
    }

    return authorizationFactory.create(authorizationId, customerId, AuthorizationFactory.PAYPAL_SDK,
        status, null, null, null, price, description, transactionId);
  }

  private Authorization getErrorAuthorization(List<ErrorResponse> errors, String authorizationId,
      String transactionId, String customerId, String type, String description, Price price) {

    Authorization authorization =
        authorizationFactory.create(authorizationId, customerId, type, Authorization.Status.FAILED,
            null, null, null, price, description, transactionId);

    if (errors == null || errors.isEmpty()) {
      return authorization;
    }

    final ErrorResponse error = errors.get(0);

    if ("PRODUCT-204".equals(error.code)
        || "PRODUCT-209".equals(error.code)
        || "PRODUCT-214".equals(error.code)) {
      authorization = authorizationFactory.create(authorizationId, customerId, type,
          Authorization.Status.PENDING, null, null, null, price, description, transactionId);
    }

    if ("PRODUCT-200".equals(error.code)) {
      authorization = authorizationFactory.create(authorizationId, customerId, type,
          Authorization.Status.ACTIVE, null, null, null, price, description, transactionId);
    }

    if ("PRODUCT-216".equals(error.code)) {
      authorization = authorizationFactory.create(authorizationId, customerId, type,
          Authorization.Status.PROCESSING, null, null, null, price, description, transactionId);
    }

    return authorization;
  }
}