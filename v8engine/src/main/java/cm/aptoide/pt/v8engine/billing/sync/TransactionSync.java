/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.billing.sync;

import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.transaction.LocalTransaction;
import cm.aptoide.pt.v8engine.billing.transaction.Transaction;
import cm.aptoide.pt.v8engine.billing.transaction.TransactionPersistence;
import cm.aptoide.pt.v8engine.billing.transaction.TransactionService;
import cm.aptoide.pt.v8engine.sync.Sync;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class TransactionSync extends Sync {

  private final Product product;
  private final TransactionPersistence transactionPersistence;
  private final Payer payer;
  private final TransactionService transactionService;
  private final String sellerId;

  public TransactionSync(Product product, TransactionPersistence transactionPersistence,
      Payer payer, TransactionService transactionService, boolean periodic, boolean exact,
      long interval, long trigger, String sellerId) {
    super(String.valueOf(product.getId()), periodic, exact, trigger, interval);
    this.product = product;
    this.transactionPersistence = transactionPersistence;
    this.payer = payer;
    this.transactionService = transactionService;
    this.sellerId = sellerId;
  }

  @Override public Completable execute() {
    return payer.getId()
        .flatMap(payerId -> syncLocalTransaction(payerId, sellerId).onErrorResumeNext(throwable -> {
          if (throwable instanceof NoSuchElementException) {
            return syncTransaction(payerId, sellerId);
          }
          return Single.error(throwable);
        }))
        .toCompletable();
  }

  private Single<Transaction> syncLocalTransaction(String payerId, String sellerId) {
    return transactionPersistence.getTransaction(sellerId, payerId, product.getId())
        .timeout(1, TimeUnit.SECONDS, Observable.empty())
        .first()
        .filter(transaction -> transaction instanceof LocalTransaction)
        .filter(transaction -> transaction.isPending())
        .cast(LocalTransaction.class)
        .toSingle()
        .flatMap(
            localTransaction -> transactionService.createTransaction(localTransaction.getSellerId(),
                localTransaction.getPayerId(), localTransaction.getPaymentMethodId(), product,
                localTransaction.getLocalMetadata(), localTransaction.getPayload()))
        .flatMap(transaction -> transactionPersistence.saveTransaction(transaction)
            .andThen(Single.just(transaction)));
  }

  private Single<Transaction> syncTransaction(String payerId, String sellerId) {
    return transactionService.getTransaction(sellerId, payerId, product)
        .flatMap(transaction -> transactionPersistence.saveTransaction(transaction)
            .andThen(Single.just(transaction)));
  }
}
