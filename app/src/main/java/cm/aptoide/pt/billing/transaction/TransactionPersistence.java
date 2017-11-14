package cm.aptoide.pt.billing.transaction;

import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public interface TransactionPersistence {

  Observable<Transaction> getTransaction(String customerId, String productId);

  Completable saveTransaction(Transaction transaction);

  Completable removeTransaction(String transactionId);

  Single<List<Transaction>> getOtherTransactions(String transactionId, String productId,
      String customerId);
}
