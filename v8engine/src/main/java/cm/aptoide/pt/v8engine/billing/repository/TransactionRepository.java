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

  private final Payer payer;
  private final TransactionPersistence transactionPersistence;
  private final BillingSyncScheduler syncScheduler;

  public TransactionRepository(TransactionPersistence transactionPersistence,
      BillingSyncScheduler syncScheduler, Payer payer) {
    this.transactionPersistence = transactionPersistence;
    this.syncScheduler = syncScheduler;
    this.payer = payer;
  }

  public abstract Completable createTransaction(int paymentMethodId, Product product);

  public Observable<Transaction> getTransaction(Product product) {
    return payer.getId()
        .flatMapObservable(payerId -> syncTransaction(product).andThen(
            transactionPersistence.getTransaction(product.getId(), payerId)));
  }

  public Completable createTransaction(Product product, int paymentMethodId, String metadata) {
    return payer.getId()
        .flatMap(payerId -> transactionPersistence.createTransaction(product.getId(), metadata,
            Transaction.Status.PENDING, payerId, paymentMethodId))
        .flatMapCompletable(transaction -> syncTransaction(product));
  }

  public Completable remove(int productId) {
    return transactionPersistence.removeTransaction(productId);
  }

  protected Completable syncTransaction(Product product) {
    return syncScheduler.scheduleTransactionSync(product);
  }
}