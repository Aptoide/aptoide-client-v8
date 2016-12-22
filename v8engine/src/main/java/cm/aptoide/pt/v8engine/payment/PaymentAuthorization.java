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
    ERROR,
    SYNCING,
    ACTIVE,
    INITIATED,
    PAYMENT_METHOD_CHANGE,
    PENDING,
    PENDING_PAYMENT_METHOD,
    REJECTED,
    CANCELLED,
    EXPIRED,
    SESSION_EXPIRED,
    CANCELLED_BY_CHARGEBACK
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
        || Status.PENDING_PAYMENT_METHOD.equals(status)
        || Status.SYNCING.equals(status);
  }

  public boolean isInvalid() {
    return Status.CANCELLED.equals(status)
        || Status.REJECTED.equals(status)
        || Status.EXPIRED.equals(status)
        || Status.SESSION_EXPIRED.equals(status)
        || Status.CANCELLED_BY_CHARGEBACK.equals(status)
        || Status.ERROR.equals(status);
  }

  public Status getStatus() {
    return status;
  }

}
