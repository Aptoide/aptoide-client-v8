/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.transaction;

import cm.aptoide.pt.billing.BillingSyncScheduler;
import cm.aptoide.pt.billing.Customer;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class TransactionRepository {

  private final Customer customer;
  private final TransactionPersistence transactionPersistence;
  private final TransactionService transactionService;
  private final BillingSyncScheduler syncScheduler;

  public TransactionRepository(TransactionPersistence transactionPersistence,
      BillingSyncScheduler syncScheduler, Customer customer,
      TransactionService transactionService) {
    this.transactionPersistence = transactionPersistence;
    this.syncScheduler = syncScheduler;
    this.customer = customer;
    this.transactionService = transactionService;
  }

  public Single<Transaction> createTransaction(long productId, int serviceId, String payload) {
    return transactionService.createTransaction(productId, serviceId, payload)
        .flatMap(transaction -> transactionPersistence.saveTransaction(transaction)
            .andThen(Single.just(transaction)));
  }

  public Observable<Transaction> getTransaction(long productId) {
    return customer.getId()
        .doOnSuccess(__ -> syncScheduler.syncTransactions())
        .flatMapObservable(
            customerId -> transactionPersistence.getTransaction(customerId, productId));
  }

  public Completable remove(long productId) {
    return customer.getId()
        .flatMapCompletable(
            customerId -> transactionPersistence.removeTransaction(customerId, productId));
  }
}