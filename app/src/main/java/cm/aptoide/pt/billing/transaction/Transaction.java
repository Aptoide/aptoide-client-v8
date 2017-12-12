package cm.aptoide.pt.billing.transaction;

public interface Transaction {
  String getCustomerId();

  String getProductId();

  String getId();

  boolean isNew();

  boolean isCompleted();

  boolean isPendingAuthorization();

  boolean isProcessing();

  boolean isFailed();

  enum Status {
    NEW, PENDING_SERVICE_AUTHORIZATION, PROCESSING, COMPLETED, FAILED
  }
}
