/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/09/2016.
 */

package cm.aptoide.pt.billing.persistence;

import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionPersistence;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class InMemoryTransactionPersistence implements TransactionPersistence {

  private final Map<String, Transaction> transactions;
  private final PublishRelay<List<Transaction>> transactionRelay;

  public InMemoryTransactionPersistence(Map<String, Transaction> transactions,
      PublishRelay<List<Transaction>> transactionRelay) {
    this.transactions = transactions;
    this.transactionRelay = transactionRelay;
  }

  @Override public Observable<Transaction> getTransaction(String customerId, String productId) {
    return transactionRelay.startWith(new ArrayList<Transaction>(transactions.values()))
        .flatMap(transactions -> Observable.from(transactions)
            .filter(transaction -> transaction.getCustomerId()
                .equals(customerId) && transaction.getProductId()
                .equals(productId)));
  }

  @Override public Completable saveTransaction(Transaction transaction) {
    return Completable.fromAction(() -> {
      transactions.put(transaction.getId(), transaction);
      transactionRelay.call(new ArrayList<>(transactions.values()));
    });
  }

  @Override public Completable removeTransaction(String transactionId) {
    return Completable.fromAction(() -> transactions.remove(transactionId));
  }

  @Override
  public Single<List<Transaction>> getOtherTransactions(String transactionId, String productId) {
    return Observable.from(transactions.values())
        .filter(transaction -> transaction.getProductId()
            .equals(productId) && !transaction.getId()
            .equals(transactionId))
        .toList()
        .toSingle();
  }
}