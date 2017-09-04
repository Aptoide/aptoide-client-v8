package cm.aptoide.pt.billing;

public interface BillingSyncScheduler {

  void syncAuthorization(int paymentId);

  void syncTransaction(String sellerId, Product product);
}
