package cm.aptoide.pt.billing.transaction;

public class Transaction {

  private final String id;
  private final String productId;
  private final String customerId;
  private final Status status;
  private final String serviceId;

  public Transaction(String id, String productId, String customerId, Status status,
      String serviceId) {
    this.productId = productId;
    this.customerId = customerId;
    this.status = status;
    this.serviceId = serviceId;
    this.id = id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getProductId() {
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

  public String getServiceId() {
    return serviceId;
  }

  public String getId() {
    return id;
  }

  public enum Status {
    NEW, PENDING_SERVICE_AUTHORIZATION, PROCESSING, COMPLETED, FAILED
  }
}