package cm.aptoide.pt.billing.transaction;

import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public interface TransactionPersistence {

  Observable<Transaction> getTransaction(String customerId, long productId);

  Completable removeTransaction(String customerId, long productId);

  Completable saveTransaction(Transaction transaction);

  Completable saveTransactions(List<Transaction> transaction);
}
