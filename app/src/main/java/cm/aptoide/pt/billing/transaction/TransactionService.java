package cm.aptoide.pt.billing.transaction;

import rx.Single;

public interface TransactionService {

  Single<Transaction> getTransaction(String productId);

  Single<Transaction> createTransaction(String productId, String serviceId, String payload);
}
