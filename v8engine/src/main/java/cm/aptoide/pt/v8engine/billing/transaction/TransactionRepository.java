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

  public Single<Transaction> createTransaction(String sellerId, int paymentMethodId,
      Product product, String payload) {
    return payer.getId()
        .flatMap(payerId -> transactionService.createTransaction(sellerId, payerId, paymentMethodId,
            product, payload))
        .flatMap(transaction -> transactionPersistence.saveTransaction(transaction)
            .andThen(Single.just(transaction)));
  }

  public Observable<Transaction> getTransaction(Product product, String sellerId) {
    return payer.getId()
        .doOnSuccess(__ -> syncScheduler.syncTransaction(sellerId, product))
        .flatMapObservable(
            payer -> transactionPersistence.getTransaction(sellerId, payer, product.getId()));
  }

  public Single<Transaction> createTransaction(String sellerId, int paymentMethodId,
      Product product, String metadata, String payload) {
    return payer.getId()
        .flatMap(
            payerId -> transactionPersistence.createTransaction(sellerId, payerId, paymentMethodId,
                product.getId(), Transaction.Status.PENDING, payload, metadata));
  }

  public Completable remove(String productId, String sellerId) {
    return payer.getId()
        .flatMapCompletable(
            payerId -> transactionPersistence.removeTransaction(sellerId, payerId, productId));
  }
}