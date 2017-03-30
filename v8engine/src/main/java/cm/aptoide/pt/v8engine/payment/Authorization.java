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
  private final String payerId;
  private Status status;

  public Authorization(int paymentId, String payerId, Status status) {
    this.paymentId = paymentId;
    this.payerId = payerId;
    this.status = status;
  }

  public String getPayerId() {
    return payerId;
  }

  public int getPaymentId() {
    return paymentId;
  }

  public boolean isAuthorized() {
    return Status.ACTIVE.equals(status) || Status.NONE.equals(status);
  }

  public boolean isPendingInitiation() {
    return Status.INACTIVE.equals(status);
  }

  public boolean isInitiated() {
    return Status.INITIATED.equals(status);
  }

  public boolean isPending() {
    return Status.PENDING.equals(status);
  }

  public boolean isFailed() {
    return Status.CANCELLED.equals(status)
        || Status.EXPIRED.equals(status)
        || Status.SESSION_EXPIRED.equals(status);
  }

  public Status getStatus() {
    return status;
  }

  public enum Status {
    NONE, INACTIVE, ACTIVE, INITIATED, PENDING, CANCELLED, EXPIRED, SESSION_EXPIRED,
  }
}
