package cm.aptoide.pt.billing.transaction;

public class TransactionFactory {

  public Transaction create(long id, String customerId, int serviceId, long productId,
      Transaction.Status status) {
    return new Transaction(id, productId, customerId, status, serviceId);
  }
}