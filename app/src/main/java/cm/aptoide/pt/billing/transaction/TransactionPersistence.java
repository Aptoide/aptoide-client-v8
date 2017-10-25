package cm.aptoide.pt.billing.transaction;

import rx.Completable;
import rx.Observable;

public interface TransactionPersistence {

  Observable<Transaction> getTransaction(String customerId, String productId);

  Completable saveTransaction(Transaction transaction);

  Completable removeTransactions(String productId);
}
