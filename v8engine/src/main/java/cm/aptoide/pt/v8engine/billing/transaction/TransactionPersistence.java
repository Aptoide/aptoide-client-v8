package cm.aptoide.pt.v8engine.billing.transaction;

import rx.Completable;
import rx.Observable;
import rx.Single;

public interface TransactionPersistence {

  Single<Transaction> createTransaction(int productId, String metadata, Transaction.Status status,
      String payerId, int paymentMethodId);

  Observable<Transaction> getTransaction(int productId, String payerId);

  Completable removeTransaction(String payerId, int productId);

  Completable saveTransaction(Transaction transaction);
}
