package cm.aptoide.pt.billing.transaction;

public class Transaction {
  private final String productId;
  private final String customerId;
  private final Status status;
  private final int paymentMethodId;
  private final String payload;
  private final String merchantName;

  public Transaction(String productId, String customerId, Status status, int paymentMethodId,
      String payload, String merchantName) {
    this.productId = productId;
    this.customerId = customerId;
    this.status = status;
    this.paymentMethodId = paymentMethodId;
    this.payload = payload;
    this.merchantName = merchantName;
  }

  public String getPayload() {
    return payload;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getProductId() {
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

  public String getSellerId() {
    return merchantName;
  }

  public enum Status {
    UNKNOWN, NEW, CREATED, PENDING_USER_AUTHORIZATION, PROCESSING, PENDING, COMPLETED, FAILED, CANCELED
  }
}
