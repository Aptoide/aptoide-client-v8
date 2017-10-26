package cm.aptoide.pt.billing;

public interface BillingSyncScheduler {

  void syncAuthorization(String transactionId);

  void syncTransaction(String productId);

  void stopSyncs();
}
