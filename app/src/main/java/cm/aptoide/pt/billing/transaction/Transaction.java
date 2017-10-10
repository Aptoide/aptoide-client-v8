package cm.aptoide.pt.billing.transaction;

public class Transaction {

  private final long id;
  private final long productId;
  private final String customerId;
  private final Status status;
  private final long serviceId;

  public Transaction(long id, long productId, String customerId, Status status, long serviceId) {
    this.productId = productId;
    this.customerId = customerId;
    this.status = status;
    this.serviceId = serviceId;
    this.id = id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public long getProductId() {
    return productId;
  }

  public boolean isNew() {
    return Status.NEW.equals(status);
  }

  public boolean isCompleted() {
    return Status.COMPLETED.equals(status);
  }

  public boolean isPendingAuthorization() {
    return Status.PENDING_SERVICE_AUTHORIZATION.equals(status);
  }

  public boolean isProcessing() {
    return Status.PROCESSING.equals(status);
  }

  public boolean isFailed() {
    return Status.FAILED.equals(status);
  }

  public long getServiceId() {
    return serviceId;
  }

  public long getId() {
    return id;
  }

  public enum Status {
    NEW, PENDING_SERVICE_AUTHORIZATION, PROCESSING, COMPLETED, FAILED
  }
}