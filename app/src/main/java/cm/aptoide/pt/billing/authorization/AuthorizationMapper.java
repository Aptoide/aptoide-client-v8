/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.Price;
import cm.aptoide.pt.database.realm.RealmAuthorization;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetAuthorizationRequest;
import java.util.List;

public class AuthorizationMapper {

  private final AuthorizationFactory authorizationFactory;

  public AuthorizationMapper(AuthorizationFactory authorizationFactory) {
    this.authorizationFactory = authorizationFactory;
  }

  public RealmAuthorization map(PayPalAuthorization authorization) {
    return new RealmAuthorization(authorization.getId(), authorization.getCustomerId(),
        authorization.getStatus()
            .name(), authorization.getTransactionId(), authorization.getMetadata(),
        authorization.getDescription(), authorization.getPrice()
        .getAmount(), authorization.getPrice()
        .getCurrency(), authorization.getPrice()
        .getCurrencySymbol(), AuthorizationFactory.PAYPAL_SDK);
  }

  public Authorization map(RealmAuthorization realmAuthorization) {
    return authorizationFactory.create(realmAuthorization.getId(),
        realmAuthorization.getCustomerId(), realmAuthorization.getType(),
        Authorization.Status.valueOf(realmAuthorization.getStatus()), null, null,
        realmAuthorization.getMetadata(),
        new Price(realmAuthorization.getAmount(), realmAuthorization.getCurrency(),
            realmAuthorization.getCurrencySymbol()), realmAuthorization.getDescription(),
        realmAuthorization.getTransactionId());
  }

  public Authorization map(GetAuthorizationRequest.ResponseBody.Authorization response) {

    Price price = null;
    if (response.getPrice() != null) {
      price = new Price(response.getPrice()
          .getAmount(), response.getPrice()
          .getCurrency(), response.getPrice()
          .getCurrencySymbol());
    }

    final GetAuthorizationRequest.ResponseBody.Authorization.Metadata metadata = response.getData();
    String url = null;
    String redirectUrl = null;
    String description = null;
    if (metadata != null) {
      url = metadata.getUrl();
      redirectUrl = metadata.getRedirectUrl();
      description = metadata.getDescription();
    }

    return authorizationFactory.create(response.getId(), String.valueOf(response.getUser()
            .getId()), response.getType(), Authorization.Status.valueOf(response.getStatus()), url,
        redirectUrl, null, price, description, response.getId());
  }

  public Authorization map(String customerId, long serviceId, long productId, String type,
      Authorization.Status status, PaidApp paidApp) {

    if (paidApp.hasErrors()) {
      return getErrorAuthorization(paidApp.getErrors(), serviceId, productId, customerId);
    }

    if (!paidApp.isPaid()) {
      return authorizationFactory.create(serviceId, customerId, type, Authorization.Status.FAILED,
          null, null, null, null, null, productId);
    }

    return authorizationFactory.create(serviceId, customerId, type, status, null, null, null,
        new Price(paidApp.getPayment()
            .getAmount(), paidApp.getPayment()
            .getPaymentServices()
            .get(0)
            .getCurrency(), paidApp.getPayment()
            .getSymbol()), paidApp.getApp()
            .getName(), productId);
  }

  public Authorization map(String customerId, long productId, String type,
      TransactionResponse transactionResponse, PaidApp paidApp) {

    if (transactionResponse.hasErrors()) {
      return getErrorAuthorization(transactionResponse.getErrors(), -1, productId, customerId);
    }

    Authorization.Status status;
    switch (transactionResponse.getTransactionStatus()) {
      case "COMPLETED":
        status = Authorization.Status.ACTIVE;
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

    return map(customerId, transactionResponse.getServiceId(), productId, type, status, paidApp);
  }

  private Authorization getErrorAuthorization(List<ErrorResponse> errors, long serviceId,
      long productId, String customerId) {

    Authorization authorization =
        authorizationFactory.create(serviceId, customerId, AuthorizationFactory.PAYPAL_SDK,
            Authorization.Status.FAILED, null, null, null, null, null, productId);

    if (errors == null || errors.isEmpty()) {
      return authorization;
    }

    final ErrorResponse error = errors.get(0);

    if ("PRODUCT-204".equals(error.code) || "PRODUCT-209".equals(error.code)) {
      authorization =
          authorizationFactory.create(serviceId, customerId, AuthorizationFactory.PAYPAL_SDK,
              Authorization.Status.PENDING, null, null, null, null, null, productId);
    }

    if ("PRODUCT-200".equals(error.code)) {
      authorization =
          authorizationFactory.create(serviceId, customerId, AuthorizationFactory.PAYPAL_SDK,
              Authorization.Status.ACTIVE, null, null, null, null, null, productId);
    }

    if ("PRODUCT-214".equals(error.code)) {
      authorization =
          authorizationFactory.create(serviceId, customerId, AuthorizationFactory.PAYPAL_SDK,
              Authorization.Status.NEW, null, null, null, null, null, productId);
    }

    if ("PRODUCT-216".equals(error.code)) {
      authorization =
          authorizationFactory.create(serviceId, customerId, AuthorizationFactory.PAYPAL_SDK,
              Authorization.Status.PROCESSING, null, null, null, null, null, productId);
    }

    if ("PRODUCT-7".equals(error.code)
        || "PRODUCT-8".equals(error.code)
        || "PRODUCT-9".equals(error.code)
        || "PRODUCT-102".equals(error.code)
        || "PRODUCT-104".equals(error.code)
        || "PRODUCT-206".equals(error.code)
        || "PRODUCT-207".equals(error.code)
        || "PRODUCT-208".equals(error.code)
        || "PRODUCT-215".equals(error.code)
        || "PRODUCT-217".equals(error.code)) {
      authorization =
          authorizationFactory.create(serviceId, customerId, AuthorizationFactory.PAYPAL_SDK,
              Authorization.Status.FAILED, null, null, null, null, null, productId);
    }

    return authorization;
  }
}