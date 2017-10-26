/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.transaction;

import cm.aptoide.pt.billing.BillingSyncScheduler;
import cm.aptoide.pt.billing.Customer;
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

  public Single<Transaction> createTransaction(String productId, String serviceId, String payload) {
    return customer.getId()
        .flatMap(
            customerId -> transactionService.createTransaction(customerId, productId, serviceId,
                payload))
        .flatMap(
            transaction -> transactionPersistence.removeTransactions(transaction.getProductId())
                .andThen(transactionPersistence.saveTransaction(transaction))
                .andThen(Single.just(transaction)));
  }

  public Single<Transaction> createTransaction(String productId, String serviceId, String payload,
      String token) {
    return customer.getId()
        .flatMap(
            customerId -> transactionService.createTransaction(customerId, productId, serviceId,
                payload, token))
        .flatMap(
            transaction -> transactionPersistence.removeTransactions(transaction.getProductId())
                .andThen(transactionPersistence.saveTransaction(transaction))
                .andThen(Single.just(transaction)));
  }

  public Observable<Transaction> getTransaction(String productId) {
    return customer.getId()
        .doOnSuccess(__ -> syncScheduler.syncTransaction(productId))
        .flatMapObservable(
            customerId -> transactionPersistence.getTransaction(customerId, productId));
  }
}