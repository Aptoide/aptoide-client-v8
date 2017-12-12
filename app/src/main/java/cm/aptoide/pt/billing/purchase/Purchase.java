package cm.aptoide.pt.billing.purchase;

public class Purchase {

  private final Status status;
  private final String productId;
  private final String transactionId;

  public Purchase(Status status, String productId, String transactionId) {
    this.status = status;
    this.productId = productId;
    this.transactionId = transactionId;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public Status getStatus() {
    return status;
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

  public enum Status {
    COMPLETED, FAILED, NEW
  }
}