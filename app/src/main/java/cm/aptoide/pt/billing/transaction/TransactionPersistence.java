package cm.aptoide.pt.billing.transaction;

import rx.Completable;
import rx.Observable;

public interface TransactionPersistence {

  Observable<Transaction> getTransaction(String customerId, long productId);

  Completable removeTransaction(String customerId, long productId);

  Completable saveTransaction(Transaction transaction);
}
