package cm.aptoide.pt.billing.transaction;

public class SimpleTransaction implements Transaction {

  private final String id;
  private final String customerId;
  private final String productId;
  private final Status status;

  public SimpleTransaction(String id, Status status, String customerId, String productId) {
    this.status = status;
    this.id = id;
    this.customerId = customerId;
    this.productId = productId;
  }

  @Override public String getCustomerId() {
    return customerId;
  }

  @Override public String getProductId() {
    return productId;
  }

  @Override public String getId() {
    return id;
  }

  @Override public boolean isNew() {
    return Status.NEW.equals(status);
  }

  @Override public boolean isCompleted() {
    return Status.COMPLETED.equals(status);
  }

  @Override public boolean isPendingAuthorization() {
    return Status.PENDING_SERVICE_AUTHORIZATION.equals(status);
  }

  @Override public boolean isProcessing() {
    return Status.PROCESSING.equals(status);
  }

  @Override public boolean isFailed() {
    return Status.FAILED.equals(status);
  }
}