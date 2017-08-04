/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.billing.sync;

import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
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
  private final BillingAnalytics analytics;
  private final TransactionService transactionService;

  public TransactionSync(Product product, TransactionPersistence transactionPersistence,
      Payer payer, BillingAnalytics analytics, TransactionService transactionService,
      boolean periodic, boolean exact, long interval, long trigger) {
    super(String.valueOf(product.getId()), periodic, exact, trigger, interval);
    this.product = product;
    this.transactionPersistence = transactionPersistence;
    this.payer = payer;
    this.analytics = analytics;
    this.transactionService = transactionService;
  }

  @Override public Completable execute() {
    return payer.getId()
        .flatMap(payerId -> syncLocalTransaction(payerId).onErrorResumeNext(throwable -> {
          if (throwable instanceof NoSuchElementException) {
            return syncTransaction(payerId);
          }
          return Single.error(throwable);
        }))
        .doOnSuccess(transaction -> analytics.sendPurchaseStatusEvent(transaction, product))
        .doOnError(throwable -> analytics.sendPurchaseErrorEvent(product, throwable))
        .toCompletable();
  }

  private Single<Transaction> syncLocalTransaction(String payerId) {
    return transactionPersistence.getTransaction(product.getId(), payerId)
        .timeout(1, TimeUnit.SECONDS, Observable.empty())
        .first()
        .filter(transaction -> transaction instanceof LocalTransaction)
        .filter(transaction -> transaction.isPending())
        .cast(LocalTransaction.class)
        .toSingle()
        .flatMap(localTransaction -> transactionService.createTransaction(product,
            localTransaction.getPaymentMethodId(), localTransaction.getPayerId(),
            localTransaction.getLocalMetadata()))
        .flatMap(transaction -> transactionPersistence.saveTransaction(transaction)
            .andThen(Single.just(transaction)));
  }

  private Single<Transaction> syncTransaction(String payerId) {
    return transactionService.getTransaction(product, payerId)
        .flatMap(transaction -> transactionPersistence.saveTransaction(transaction)
            .andThen(Single.just(transaction)));
  }
}
