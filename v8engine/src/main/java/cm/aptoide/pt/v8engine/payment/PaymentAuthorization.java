/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 15/11/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.model.v3.GetProductPurchaseAuthorizationResponse;

/**
 * Created by marcelobenites on 15/11/16.
 */
public class PaymentAuthorization {

  private final int paymentId;
  private final String url;
  private final String redirectUrl;
  private GetProductPurchaseAuthorizationResponse.Status status;

  public PaymentAuthorization(int paymentId, String url, String redirectUrl, GetProductPurchaseAuthorizationResponse.Status status) {
    this.paymentId = paymentId;
    this.url = url;
    this.redirectUrl = redirectUrl;
    this.status = status;
  }

  public int getPaymentId() {
    return paymentId;
  }

  public String getUrl() {
    return url;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  public boolean isAuthorized() {
    return GetProductPurchaseAuthorizationResponse.Status.ACTIVE.equals(status);
  }

  public boolean displayAuthorizationView() {
    return GetProductPurchaseAuthorizationResponse.Status.INITIATED.equals(status);
  }

  public boolean isCancelled() {
    return GetProductPurchaseAuthorizationResponse.Status.CANCELLED.equals(status)
        || GetProductPurchaseAuthorizationResponse.Status.CANCELLED_BY_CHARGEBACK.equals(status)
        || GetProductPurchaseAuthorizationResponse.Status.EXPIRED.equals(status)
        || GetProductPurchaseAuthorizationResponse.Status.REJECTED.equals(status);
  }

  public GetProductPurchaseAuthorizationResponse.Status getStatus() {
    return status;
  }
}
