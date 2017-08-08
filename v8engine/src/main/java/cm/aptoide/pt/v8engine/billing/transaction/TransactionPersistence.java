package cm.aptoide.pt.v8engine.billing.transaction;

import rx.Completable;
import rx.Observable;
import rx.Single;

public interface TransactionPersistence {

  Single<Transaction> createTransaction(String productId, String metadata, Transaction.Status status,
      String payerId, int paymentMethodId, String payload);

  Observable<Transaction> getTransaction(String productId, String payerId);

  Completable removeTransaction(String payerId, String productId);

  Completable saveTransaction(Transaction transaction);
}
