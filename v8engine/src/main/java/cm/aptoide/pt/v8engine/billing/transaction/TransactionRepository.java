/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.transaction;

import cm.aptoide.pt.v8engine.billing.BillingSyncScheduler;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class TransactionRepository {

  private final Payer payer;
  private final TransactionPersistence transactionPersistence;
  private final TransactionService transactionService;
  private final BillingSyncScheduler syncScheduler;

  public TransactionRepository(TransactionPersistence transactionPersistence,
      BillingSyncScheduler syncScheduler, Payer payer, TransactionService transactionService) {
    this.transactionPersistence = transactionPersistence;
    this.syncScheduler = syncScheduler;
    this.payer = payer;
    this.transactionService = transactionService;
  }

  public Single<Transaction> createTransaction(int paymentMethodId, Product product) {
    return payer.getId()
        .flatMap(payerId -> transactionService.createTransaction(product, paymentMethodId, payerId))
        .flatMap(transaction -> transactionPersistence.saveTransaction(transaction)
            .andThen(Single.just(transaction)));
  }

  public Observable<Transaction> getTransaction(Product product) {
    return payer.getId()
        .doOnSuccess(__ -> syncScheduler.syncTransaction(product))
        .flatMapObservable(payer -> transactionPersistence.getTransaction(product.getId(), payer));
  }

  public Single<Transaction> createTransaction(int paymentMethodId, Product product,
      String metadata) {
    return payer.getId()
        .flatMap(payerId -> transactionPersistence.createTransaction(product.getId(), metadata,
            Transaction.Status.PENDING, payerId, paymentMethodId));
  }

  public Completable remove(int productId) {
    return payer.getId()
        .flatMapCompletable(
            payerId -> transactionPersistence.removeTransaction(payerId, productId));
  }
}