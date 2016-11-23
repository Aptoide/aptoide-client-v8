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
    ACTIVE,
    INITIATED,
    PAYMENT_METHOD_CHANGE,
    PENDING,
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

  public boolean isPending() {
    return Status.PENDING.equals(status)
        || Status.PENDING_PAYMENT_METHOD.equals(status);
  }

  public Status getStatus() {
    return status;
  }

}
