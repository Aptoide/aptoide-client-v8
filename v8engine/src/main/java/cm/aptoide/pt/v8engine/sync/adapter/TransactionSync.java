/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync.adapter;

import android.content.SyncResult;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.Transaction;
import cm.aptoide.pt.v8engine.billing.TransactionPersistence;
import cm.aptoide.pt.v8engine.billing.TransactionService;
import cm.aptoide.pt.v8engine.billing.methods.paypal.PayPalTransaction;
import java.io.IOException;
import rx.Observable;
import rx.Single;

public class TransactionSync extends ScheduledSync {

  private final Product product;
  private final TransactionPersistence transactionPersistence;
  private final Payer payer;
  private final BillingAnalytics analytics;
  private final TransactionService transactionService;

  public TransactionSync(Product product, TransactionPersistence transactionPersistence,
      Payer payer, BillingAnalytics analytics, TransactionService transactionService) {
    this.product = product;
    this.transactionPersistence = transactionPersistence;
    this.payer = payer;
    this.analytics = analytics;
    this.transactionService = transactionService;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      payer.getId()
          .flatMapObservable(payerId -> syncPayPalTransaction(payerId).switchIfEmpty(
              syncTransaction(payerId).toObservable())
              .doOnNext(transaction -> {
                analytics.sendPurchaseStatusEvent(transaction, product);
                reschedulePendingTransaction(transaction, syncResult);
              })
              .doOnError(throwable -> {
                rescheduleOnNetworkError(syncResult, throwable);
              }))
          .toCompletable()
          .onErrorComplete()
          .await();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
  }

  public Observable<Transaction> syncPayPalTransaction(String payerId) {
    return transactionPersistence.getTransaction(product.getId(), payerId)
        .first()
        .filter(transaction -> transaction.isPending())
        .filter(transaction -> transaction instanceof PayPalTransaction)
        .cast(PayPalTransaction.class)
        .flatMapSingle(payPalTransaction -> transactionService.createTransaction(product,
            payPalTransaction.getPaymentMethodId(), payPalTransaction.getPayerId(),
            payPalTransaction.getPayPalConfirmationId())
            .flatMap(transaction -> transactionPersistence.saveTransaction(transaction)
                .andThen(Single.just(transaction))));
  }

  public Single<Transaction> syncTransaction(String payerId) {
    return transactionService.getTransaction(product, payerId)
        .flatMap(transaction -> transactionPersistence.saveTransaction(transaction)
            .andThen(Single.just(transaction)))
        .onErrorResumeNext(throwable -> {
          if (!(throwable instanceof IOException)) {
            return transactionPersistence.createTransaction(product.getId(), payerId);
          }
          return Single.error(throwable);
        });
  }

  private void reschedulePendingTransaction(Transaction transaction, SyncResult syncResult) {
    if (transaction.isPending()) {
      rescheduleSync(syncResult);
    }
  }

  private void rescheduleOnNetworkError(SyncResult syncResult, Throwable throwable) {
    if (throwable instanceof IOException) {
      analytics.sendPurchaseNetworkRetryEvent(product);
      rescheduleSync(syncResult);
    }
  }
}
