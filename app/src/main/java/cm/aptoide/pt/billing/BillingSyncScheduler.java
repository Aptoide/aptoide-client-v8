package cm.aptoide.pt.billing;

public interface BillingSyncScheduler {

  void syncAuthorization(long transactionId);

  void syncTransaction(long productId);
}
