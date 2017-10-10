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

  private final Map<Long, Transaction> transactions;
  private final PublishRelay<List<Transaction>> transactionRelay;
  private final TransactionFactory transactionFactory;

  public InMemoryTransactionPersistence(Map<Long, Transaction> transactions,
      PublishRelay<List<Transaction>> transactionRelay, TransactionFactory transactionFactory) {
    this.transactions = transactions;
    this.transactionRelay = transactionRelay;
    this.transactionFactory = transactionFactory;
  }

  @Override public Observable<Transaction> getTransaction(String customerId, long productId) {
    return transactionRelay.startWith(new ArrayList<Transaction>(transactions.values()))
        .flatMap(transactions -> Observable.from(transactions)
            .filter(transaction -> transaction.getCustomerId()
                .equals(customerId) && transaction.getProductId() == productId)
            .defaultIfEmpty(
                transactionFactory.create(-1, customerId, -1, productId, Transaction.Status.NEW)));
  }

  @Override public Completable removeTransaction(String customerId, long productId) {
    return Observable.from(transactions.values())
        .filter(
            transaction -> transaction.getProductId() == productId && transaction.getCustomerId()
                .equals(customerId))
        .doOnNext(transaction -> transactions.remove(transaction.getId()))
        .toList()
        .toCompletable();
  }

  @Override public Completable saveTransaction(Transaction transaction) {
    return Completable.fromAction(() -> {
      transactions.put(transaction.getId(), transaction);
      transactionRelay.call(new ArrayList<>(transactions.values()));
    });
  }
}