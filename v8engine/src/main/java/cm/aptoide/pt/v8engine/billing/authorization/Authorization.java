/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 02/01/2017.
 */

package cm.aptoide.pt.v8engine.billing.authorization;

public class Authorization {

  private final int paymentId;
  private final String payerId;
  private final Status status;

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

  public boolean isPending() {
    return Status.PENDING.equals(status) || isInitialized();
  }

  public boolean isInactive() {
    return Status.INACTIVE.equals(status)
        || Status.EXPIRED.equals(status)
        || Status.SESSION_EXPIRED.equals(status);
  }

  public boolean isInitialized() {
    return Status.INITIATED.equals(status);
  }

  public boolean isFailed() {
    return Status.CANCELLED.equals(status) || Status.UNKNOWN_ERROR.equals(status);
  }

  public Status getStatus() {
    return status;
  }

  public enum Status {
    UNKNOWN, NONE, INACTIVE, ACTIVE, INITIATED, PENDING, CANCELLED, EXPIRED, SESSION_EXPIRED, UNKNOWN_ERROR
  }
}
