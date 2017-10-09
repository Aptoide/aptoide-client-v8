/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/09/2016.
 */

package cm.aptoide.pt.billing.transaction;

import com.jakewharton.rxrelay.PublishRelay;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import rx.Completable;
import rx.Observable;

public class InMemoryTransactionPersistence implements TransactionPersistence {

  private final Map<Long, Transaction> inMemoryTransactions;
  private final PublishRelay<List<Transaction>> transactionRelay;
  private final TransactionFactory transactionFactory;

  public InMemoryTransactionPersistence(Map<Long, Transaction> inMemoryTransactions,
      PublishRelay<List<Transaction>> transactionRelay, TransactionFactory transactionFactory) {
    this.inMemoryTransactions = inMemoryTransactions;
    this.transactionRelay = transactionRelay;
    this.transactionFactory = transactionFactory;
  }

  @Override public Observable<Transaction> getTransaction(String customerId, long productId) {
    return transactionRelay.startWith(new ArrayList<Transaction>(inMemoryTransactions.values()))
        .flatMap(transactions -> Observable.from(transactions)
            .filter(transaction -> transaction.getCustomerId()
                .equals(customerId) && transaction.getProductId() == productId)
            .defaultIfEmpty(
                transactionFactory.create(-1, customerId, -1, productId, Transaction.Status.NEW)));
  }

  @Override public Completable removeTransaction(String customerId, long productId) {
    return Observable.from(inMemoryTransactions.values())
        .filter(
            transaction -> transaction.getProductId() == productId && transaction.getCustomerId()
                .equals(customerId))
        .doOnNext(transaction -> inMemoryTransactions.remove(transaction.getId()))
        .toList()
        .toCompletable();
  }

  @Override public Completable saveTransaction(Transaction transaction) {
    return Completable.fromAction(() -> {
      inMemoryTransactions.put(transaction.getId(), transaction);
      transactionRelay.call(new ArrayList<>(inMemoryTransactions.values()));
    });
  }

  @Override public Completable saveTransactions(List<Transaction> transactions) {
    return Observable.from(transactions)
        .doOnNext(transaction -> inMemoryTransactions.put(transaction.getId(), transaction))
        .toList()
        .toCompletable()
        .doOnCompleted(() -> transactionRelay.call(new ArrayList<>(inMemoryTransactions.values())));
  }
}