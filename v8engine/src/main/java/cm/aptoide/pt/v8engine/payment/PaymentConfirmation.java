/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentConfirmation {

  private final int paymentId;
  private final int productId;
  private final String payerId;
  private final String paymentConfirmationId;
  private Status status;

  public PaymentConfirmation(int paymentId, int productId, String payerId, String paymentConfirmationId,
      Status status) {
    this.paymentId = paymentId;
    this.productId = productId;
    this.payerId = payerId;
    this.paymentConfirmationId = paymentConfirmationId;
    this.status = status;
  }

  public int getPaymentId() {
    return paymentId;
  }

  public String getPayerId() {
    return payerId;
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
        || Status.CANCELED.equals(status);
  }

  public boolean isNew() {
    return Status.NEW.equals(status);
  }

  public enum Status {
    NEW,
    CREATED,
    PROCESSING,
    PENDING,
    COMPLETED,
    FAILED,
    CANCELED
  }
}
