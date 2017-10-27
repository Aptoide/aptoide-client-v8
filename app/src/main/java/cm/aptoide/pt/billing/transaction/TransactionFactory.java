package cm.aptoide.pt.billing.transaction;

public class TransactionFactory {

  public Transaction create(String id, String customerId, String serviceId, String productId,
      Transaction.Status status) {
    return new Transaction(id, productId, customerId, status, serviceId);
  }
}