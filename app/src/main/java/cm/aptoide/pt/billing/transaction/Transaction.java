package cm.aptoide.pt.billing.transaction;

public class Transaction {

  private final long id;
  private final long productId;
  private final String customerId;
  private final Status status;
  private final int serviceId;

  public Transaction(long id, long productId, String customerId, Status status, int serviceId) {
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
    return Status.PENDING_SERVICE_AUTHORIZATION.equals(status);
  }

  public boolean isPending() {
    return Status.CREATED.equals(status)
        || Status.PROCESSING.equals(status)
        || Status.PENDING.equals(status);
  }

  public boolean isFailed() {
    return Status.CANCELED.equals(status) || Status.FAILED.equals(status);
  }

  public int getServiceId() {
    return serviceId;
  }

  public boolean isUnknown() {
    return Status.UNKNOWN.equals(status);
  }

  public long getId() {
    return id;
  }

  public enum Status {
    UNKNOWN, NEW, CREATED, PENDING_SERVICE_AUTHORIZATION, PROCESSING, PENDING, COMPLETED, FAILED, CANCELED
  }
}
