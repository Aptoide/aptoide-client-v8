/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentConfirmation {

  public static enum Status {
    SYNCING_ERROR,
    CREATED,
    PROCESSING,
    PENDING,
    COMPLETED,
    FAILED,
    CANCELED
  }

  private final int productId;
  private final String paymentConfirmationId;

  private Status status;

  public PaymentConfirmation(int productId, String paymentConfirmationId, Status status) {
    this.productId = productId;
    this.paymentConfirmationId = paymentConfirmationId;
    this.status = status;
  }

  public int getProductId() {
    return productId;
  }

  public String getPaymentConfirmationId() {
    return paymentConfirmationId;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public boolean isCompleted() {
    return Status.COMPLETED.equals(status);
  }

  public boolean isPending() {
    return Status.CREATED.equals(status)
        || Status.PROCESSING.equals(status)
        || Status.PENDING.equals(status);
  }
  public boolean isFailed() {
    return Status.FAILED.equals(status)
        || Status.CANCELED.equals(status)
        || Status.SYNCING_ERROR.equals(status);
  }
}