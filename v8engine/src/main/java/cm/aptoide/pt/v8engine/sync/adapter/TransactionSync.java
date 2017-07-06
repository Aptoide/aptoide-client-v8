/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync.adapter;

import android.content.SharedPreferences;
import android.content.SyncResult;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaymentConfirmationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.Transaction;
import cm.aptoide.pt.v8engine.billing.TransactionPersistence;
import cm.aptoide.pt.v8engine.billing.methods.paypal.PayPalTransaction;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.billing.repository.TransactionFactory;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.io.IOException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

public class TransactionSync extends ScheduledSync {

  private final Product product;
  private final TransactionPersistence transactionPersistence;
  private final TransactionFactory transactionFactory;
  private final Payer payer;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final Converter.Factory converterFactory;
  private final OkHttpClient httpClient;
  private final BillingAnalytics analytics;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public TransactionSync(Product product, TransactionPersistence transactionPersistence,
      TransactionFactory transactionFactory, Payer payer,
      BodyInterceptor<BaseBody> bodyInterceptorV3, Converter.Factory converterFactory,
      OkHttpClient httpClient, BillingAnalytics analytics, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.product = product;
    this.transactionPersistence = transactionPersistence;
    this.transactionFactory = transactionFactory;
    this.payer = payer;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.analytics = analytics;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      payer.getId()
          .flatMapObservable(
              payerId -> transactionPersistence.getTransaction(product.getId(), payerId)
                  .first()
                  .toSingle()
                  .flatMapObservable(
                      persistedTransactions -> Observable.from(persistedTransactions))
                  .map(persistedTransaction -> transactionFactory.map(persistedTransaction))
                  .filter(transaction -> transaction.isPending())
                  .filter(transaction -> transaction instanceof PayPalTransaction)
                  .cast(PayPalTransaction.class)
                  .flatMapSingle(transaction -> createServerTransaction(product, transaction))
                  .switchIfEmpty(getServerTransaction(product, payerId).toObservable()
                      .doOnError(
                          throwable -> saveAsNewTransactionOnServerError(payerId, throwable)))
                  .doOnNext(transaction -> {
                    analytics.sendPurchaseStatusEvent(transaction, product);
                    saveTransaction(transaction);
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

  private Single<Transaction> getServerTransaction(Product product, String payerId) {
    return Single.just(product instanceof InAppProduct)
        .flatMap(isInAppBilling -> {
          if (isInAppBilling) {
            return GetTransactionRequest.of(product.getId(),
                ((InAppProduct) product).getApiVersion(), bodyInterceptorV3, httpClient,
                converterFactory, tokenInvalidator, sharedPreferences)
                .observe()
                .cast(TransactionResponse.class)
                .toSingle();
          }
          return GetTransactionRequest.of(product.getId(), bodyInterceptorV3, httpClient,
              converterFactory, tokenInvalidator, sharedPreferences)
              .observe()
              .toSingle();
        })
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(transactionFactory.map(product.getId(), response, payerId));
          }
          return Single.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
        });
  }

  private Single<Transaction> createServerTransaction(Product product,
      PayPalTransaction transaction) {
    return Single.just(product instanceof InAppProduct)
        .flatMap(isInAppBilling -> {
          if (isInAppBilling) {
            return CreatePaymentConfirmationRequest.ofInApp(product.getId(),
                transaction.getPaymentMethodId(), ((InAppProduct) product).getDeveloperPayload(),
                transaction.getPayPalConfirmationId(), bodyInterceptorV3, httpClient,
                converterFactory, tokenInvalidator, sharedPreferences,
                ((InAppProduct) product).getPackageVersionCode())
                .observe(true)
                .toSingle();
          }
          return CreatePaymentConfirmationRequest.ofPaidApp(product.getId(),
              transaction.getPaymentMethodId(), ((PaidAppProduct) product).getStoreName(),
              transaction.getPayPalConfirmationId(), bodyInterceptorV3, httpClient,
              converterFactory, tokenInvalidator, sharedPreferences,
              ((PaidAppProduct) product).getPackageVersionCode())
              .observe(true)
              .toSingle();
        })
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(transactionFactory.create(transaction.getProductId(),
                transaction.getPayPalConfirmationId(), Transaction.Status.COMPLETED,
                transaction.getPayerId(), transaction.getPaymentMethodId()));
          }
          return Single.error(new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
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

  private void saveAsNewTransactionOnServerError(String payerId, Throwable throwable) {
    if (!(throwable instanceof IOException)) {
      saveTransaction(
          transactionFactory.create(product.getId(), Transaction.Status.NEW, payerId, -1));
    }
  }

  private void saveTransaction(Transaction transaction) {
    transactionPersistence.saveTransaction(transactionFactory.map(transaction));
  }
}
