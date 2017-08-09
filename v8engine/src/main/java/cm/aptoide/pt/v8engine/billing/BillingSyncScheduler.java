package cm.aptoide.pt.v8engine.billing;

public interface BillingSyncScheduler {

  void syncAuthorization(int paymentId);

  void syncTransaction(String sellerId, Product product);
}
