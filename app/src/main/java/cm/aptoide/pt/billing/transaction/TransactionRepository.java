/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.transaction;

import cm.aptoide.pt.billing.BillingSyncScheduler;
import cm.aptoide.pt.billing.Customer;
import cm.aptoide.pt.billing.Product;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class TransactionRepository {

  private final Customer customer;
  private final TransactionPersistence transactionPersistence;
  private final TransactionService transactionService;
  private final BillingSyncScheduler syncScheduler;

  public TransactionRepository(TransactionPersistence transactionPersistence,
      BillingSyncScheduler syncScheduler, Customer customer, TransactionService transactionService) {
    this.transactionPersistence = transactionPersistence;
    this.syncScheduler = syncScheduler;
    this.customer = customer;
    this.transactionService = transactionService;
  }

  public Single<Transaction> createTransaction(String merchantName, int paymentMethodId,
      Product product, String payload) {
    return customer.getId()
        .flatMap(customerId -> transactionService.createTransaction(merchantName, customerId, paymentMethodId,
            product, payload))
        .flatMap(transaction -> transactionPersistence.saveTransaction(transaction)
            .andThen(Single.just(transaction)));
  }

  public Observable<Transaction> getTransaction(Product product, String merchantName) {
    return customer.getId()
        .doOnSuccess(__ -> syncScheduler.syncTransaction(merchantName, product))
        .flatMapObservable(
            customer -> transactionPersistence.getTransaction(merchantName, customer, product.getId()));
  }

  public Single<Transaction> createTransaction(String merchantName, int paymentMethodId,
      Product product, String metadata, String payload) {
    return customer.getId()
        .flatMap(
            customerId -> transactionPersistence.createTransaction(merchantName, customerId, paymentMethodId,
                product.getId(), Transaction.Status.PENDING, payload, metadata));
  }

  public Completable remove(String productId, String merchantName) {
    return customer.getId()
        .flatMapCompletable(
            customerId -> transactionPersistence.removeTransaction(merchantName, customerId, productId));
  }
}