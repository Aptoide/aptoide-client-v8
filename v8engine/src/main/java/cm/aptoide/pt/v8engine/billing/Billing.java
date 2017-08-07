/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.v8engine.billing.authorization.Authorization;
import cm.aptoide.pt.v8engine.billing.authorization.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodNotAuthorizedException;
import cm.aptoide.pt.v8engine.billing.product.InAppPurchase;
import cm.aptoide.pt.v8engine.billing.transaction.Transaction;
import cm.aptoide.pt.v8engine.billing.transaction.TransactionRepository;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class Billing {

  private final TransactionRepository transactionRepository;
  private final BillingService billingService;
  private final AuthorizationRepository authorizationRepository;
  private final PaymentMethodSelector paymentMethodSelector;
  private final Payer payer;

  public Billing(TransactionRepository transactionRepository, BillingService billingService,
      AuthorizationRepository authorizationRepository, PaymentMethodSelector paymentMethodSelector,
      Payer payer) {
    this.transactionRepository = transactionRepository;
    this.billingService = billingService;
    this.authorizationRepository = authorizationRepository;
    this.paymentMethodSelector = paymentMethodSelector;
    this.payer = payer;
  }

  public Payer getPayer() {
    return payer;
  }

  public Single<Boolean> isSupported(String packageName, int apiVersion, String type) {
    return billingService.getBilling(apiVersion, packageName, type)
        .andThen(Single.just(true))
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof IllegalArgumentException) {
            return Single.just(false);
          }
          return Single.error(throwable);
        });
  }

  public Single<Product> getProduct(long appId, String storeName, boolean sponsored) {
    return billingService.getProduct(appId, sponsored, storeName);
  }

  public Single<Product> getProduct(String packageName, int apiVersion, String sku,
      String developerPayload) {
    return billingService.getProduct(apiVersion, packageName, sku, developerPayload);
  }

  public Single<List<Product>> getProducts(String packageName, int apiVersion, List<String> skus) {
    return billingService.getProducts(apiVersion, packageName, skus);
  }

  public Single<List<Purchase>> getPurchases(String packageName, int apiVersion) {
    return billingService.getPurchases(apiVersion, packageName);
  }

  public Completable consumePurchase(String packageName, int apiVersion, String purchaseToken) {
    return billingService.getPurchases(apiVersion, packageName)
        .flatMapObservable(purchases -> Observable.from(purchases))
        .cast(InAppPurchase.class)
        .filter(purchase -> purchaseToken.equals(purchase.getToken()))
        .first()
        .toSingle()
        .flatMapCompletable(purchase -> getProduct(packageName, apiVersion, purchase.getSku(),
            null).flatMapCompletable(
            product -> billingService.deletePurchase(apiVersion, packageName, purchaseToken)
                .andThen(transactionRepository.remove(product.getId()))));
  }

  public Single<List<PaymentMethod>> getPaymentMethods(Product product) {
    return billingService.getPaymentMethods(product);
  }

  public Completable processPayment(int paymentMethodId, Product product) {
    return transactionRepository.createTransaction(paymentMethodId, product)
        .flatMapCompletable(transaction -> {
          if (transaction.isPendingAuthorization()) {
            return Completable.error(
                new PaymentMethodNotAuthorizedException("Pending payment method authorization."));
          }

          if (transaction.isFailed()) {
            return Completable.error(new PaymentFailureException("Payment failed."));
          }

          return Completable.complete();
        });
  }

  public Completable processLocalPayment(int paymentMethodId, Product product,
      String localMetadata) {
    return getPaymentMethod(paymentMethodId, product).flatMapCompletable(
        payment -> transactionRepository.createTransaction(paymentMethodId, product, localMetadata)
            .toCompletable());
  }

  public Observable<Transaction> getTransaction(Product product) {
    return transactionRepository.getTransaction(product);
  }

  public Single<Purchase> getPurchase(Product product) {
    return billingService.getPurchase(product)
        .flatMap(purchase -> {
          if (purchase.isCompleted()) {
            return Single.just(purchase);
          }
          return Single.error(new IllegalArgumentException("Purchase is not completed"));
        })
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof IllegalArgumentException) {
            return transactionRepository.remove(product.getId())
                .andThen(Single.error(throwable));
          }
          return Single.error(throwable);
        });
  }

  public Observable<Authorization> getAuthorization(int paymentMethodId) {
    return authorizationRepository.getAuthorization(paymentMethodId);
  }

  public Completable authorize(int paymentMethodId) {
    return authorizationRepository.createAuthorization(paymentMethodId)
        .toCompletable();
  }

  public Completable selectPaymentMethod(int paymentMethodId, Product product) {
    return getPaymentMethod(paymentMethodId, product).flatMapCompletable(
        paymentMethod -> paymentMethodSelector.selectPaymentMethod(paymentMethod));
  }

  public Single<PaymentMethod> getSelectedPaymentMethod(Product product) {
    return getPaymentMethods(product).flatMap(
        paymentMethods -> paymentMethodSelector.selectedPaymentMethod(paymentMethods));
  }

  private Single<PaymentMethod> getPaymentMethod(int paymentMethodId, Product product) {
    return getPaymentMethods(product).flatMapObservable(payments -> Observable.from(payments)
        .filter(payment -> payment.getId() == paymentMethodId)
        .switchIfEmpty(Observable.error(
            new IllegalArgumentException("Payment " + paymentMethodId + " not found."))))
        .first()
        .toSingle();
  }
}