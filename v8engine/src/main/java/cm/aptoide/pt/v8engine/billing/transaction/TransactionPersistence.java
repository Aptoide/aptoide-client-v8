package cm.aptoide.pt.v8engine.billing.transaction;

import rx.Completable;
import rx.Observable;
import rx.Single;

public interface TransactionPersistence {

  Single<Transaction> createTransaction(String sellerId, String payerId, int paymentMethodId,
      String productId, Transaction.Status status, String payload, String metadata);

  Observable<Transaction> getTransaction(String sellerId, String payerId, String productId);

  Completable removeTransaction(String sellerId, String payerId, String productId);

  Completable saveTransaction(Transaction transaction);
}
