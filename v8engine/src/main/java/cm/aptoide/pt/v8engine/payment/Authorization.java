/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 02/01/2017.
 */

package cm.aptoide.pt.v8engine.payment;

/**
 * Created by marcelobenites on 23/12/16.
 */
public abstract class Authorization {

  private final int paymentId;
  private Status status;

  public Authorization(int paymentId, Status status) {
    this.paymentId = paymentId;
    this.status = status;
  }

  public int getPaymentId() {
    return paymentId;
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
        || Status.SYNCING_ERROR.equals(status);
  }

  public Status getStatus() {
    return status;
  }

  public abstract void authorize();

  public enum Status {
    SYNCING,
    SYNCING_ERROR,
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
}
