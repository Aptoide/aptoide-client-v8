package cm.aptoide.pt.v8engine.billing;

import rx.Completable;
import rx.Observable;
import rx.Single;

public interface TransactionPersistence {

  Single<Transaction> createTransaction(int productId, String metadata, Transaction.Status status,
      String payerId, int paymentMethodId);

  Observable<Transaction> getTransaction(int productId, String payerId);

  Completable removeTransaction(int productId);

  Completable removeAllTransactions();

  Completable saveTransaction(Transaction transaction);
}
