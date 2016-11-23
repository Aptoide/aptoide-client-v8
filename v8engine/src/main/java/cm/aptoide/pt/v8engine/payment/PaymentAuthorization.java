/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 15/11/2016.
 */

package cm.aptoide.pt.v8engine.payment;

/**
 * Created by marcelobenites on 15/11/16.
 */
public class PaymentAuthorization {

  public enum Status {
    INITIATED, PENDING, ACTIVE, EXPIRED, CANCELLED,
    CANCELLED_BY_CHARGEBACK, REJECTED,
    PAYMENT_METHOD_CHANGE,
    PENDING_PAYMENT_METHOD
  }

  private final int paymentId;
  private final String url;
  private final String redirectUrl;
  private Status status;

  public PaymentAuthorization(int paymentId, String url, String redirectUrl, Status status) {
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
    return Status.ACTIVE.equals(status);
  }

  public boolean displayAuthorizationView() {
    return Status.INITIATED.equals(status);
  }

  public boolean isCancelled() {
    return Status.CANCELLED.equals(status)
        || Status.CANCELLED_BY_CHARGEBACK.equals(status)
        || Status.EXPIRED.equals(status)
        || Status.REJECTED.equals(status);
  }

  public Status getStatus() {
    return status;
  }

}
