/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.v8engine.billing.BillingSyncScheduler;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.Transaction;
import cm.aptoide.pt.v8engine.billing.TransactionPersistence;
import cm.aptoide.pt.v8engine.billing.TransactionService;
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
            .andThen(syncTransaction(product))
            .andThen(Single.just(transaction)));
  }

  public Observable<Transaction> getTransaction(Product product) {
    return payer.getId()
        .flatMapObservable(payerId -> syncTransaction(product).andThen(
            transactionPersistence.getTransaction(product.getId(), payerId)));
  }

  public Single<Transaction> createTransaction(int paymentMethodId, Product product,
      String metadata) {
    return payer.getId()
        .flatMap(payerId -> transactionPersistence.createTransaction(product.getId(), metadata,
            Transaction.Status.PENDING, payerId, paymentMethodId))
        .flatMap(transaction -> syncTransaction(product).andThen(Single.just(transaction)));
  }

  public Completable remove(int productId) {
    return transactionPersistence.removeTransaction(productId);
  }

  private Completable syncTransaction(Product product) {
    return syncScheduler.scheduleTransactionSync(product);
  }
}