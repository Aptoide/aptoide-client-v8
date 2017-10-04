package cm.aptoide.pt.billing.transaction;

import rx.Completable;
import rx.Observable;
import rx.Single;

public interface TransactionPersistence {

  Single<Transaction> createTransaction(String sellerId, String customerId, int paymentMethodId,
      String productId, Transaction.Status status, String payload, String metadata);

  Observable<Transaction> getTransaction(String sellerId, String customerId, String productId);

  Completable removeTransaction(String sellerId, String customerId, String productId);

  Completable saveTransaction(Transaction transaction);
}
