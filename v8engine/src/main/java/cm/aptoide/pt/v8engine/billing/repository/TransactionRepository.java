/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.Transaction;
import cm.aptoide.pt.v8engine.billing.TransactionPersistence;
import rx.Completable;
import rx.Observable;

public abstract class TransactionRepository {

  protected final TransactionFactory transactionFactory;
  private final Payer payer;
  private final TransactionPersistence transactionPersistence;
  private final BillingSyncScheduler syncScheduler;

  public TransactionRepository(TransactionPersistence transactionPersistence,
      BillingSyncScheduler syncScheduler, TransactionFactory transactionFactory, Payer payer) {
    this.transactionPersistence = transactionPersistence;
    this.syncScheduler = syncScheduler;
    this.transactionFactory = transactionFactory;
    this.payer = payer;
  }

  public abstract Completable createTransaction(int paymentId, Product product);

  public Observable<Transaction> getTransaction(Product product) {
    return payer.getId()
        .flatMapObservable(payerId -> syncTransaction(product).andThen(
            transactionPersistence.getTransaction(product.getId(), payerId)
                .flatMap(paymentConfirmations -> Observable.from(paymentConfirmations)
                    .map(paymentConfirmation -> transactionFactory.map(paymentConfirmation))
                    .switchIfEmpty(syncTransaction(product).toObservable()))));
  }

  public Completable createTransaction(Product product, int paymentMethodId,
      String payPalConfirmationId) {
    return payer.getId()
        .map(payerId -> transactionFactory.create(product.getId(), payPalConfirmationId,
            Transaction.Status.PENDING, payerId, paymentMethodId))
        .doOnSuccess(transaction -> transactionPersistence.saveTransaction(
            transactionFactory.map(transaction)))
        .flatMapCompletable(transaction -> syncTransaction(product));
  }

  public Completable remove(int productId) {
    return Completable.fromAction(() -> transactionPersistence.removeTransaction(productId));
  }

  protected Completable syncTransaction(Product product) {
    return syncScheduler.scheduleTransactionSync(product);
  }
}