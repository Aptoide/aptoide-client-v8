package cm.aptoide.pt.v8engine.billing.transaction;

public class Transaction {
  private final int productId;
  private final String payerId;
  private final Status status;
  private final int paymentMethodId;

  public Transaction(int productId, String payerId, Status status, int paymentMethodId) {
    this.productId = productId;
    this.payerId = payerId;
    this.status = status;
    this.paymentMethodId = paymentMethodId;
  }

  public String getPayerId() {
    return payerId;
  }

  public int getProductId() {
    return productId;
  }

  public Status getStatus() {
    return status;
  }

  public boolean isNew() {
    return Status.NEW.equals(status);
  }

  public boolean isCompleted() {
    return Status.COMPLETED.equals(status);
  }

  public boolean isPendingAuthorization() {
    return Status.PENDING_USER_AUTHORIZATION.equals(status);
  }

  public boolean isPending() {
    return Status.CREATED.equals(status)
        || Status.PROCESSING.equals(status)
        || Status.PENDING.equals(status);
  }

  public boolean isFailed() {
    return Status.CANCELED.equals(status) || Status.FAILED.equals(status);
  }

  public int getPaymentMethodId() {
    return paymentMethodId;
  }

  public boolean isUnknown() {
    return Status.UNKNOWN.equals(status);
  }

  public enum Status {
    UNKNOWN, NEW, CREATED, PENDING_USER_AUTHORIZATION, PROCESSING, PENDING, COMPLETED, FAILED, CANCELED
  }
}
