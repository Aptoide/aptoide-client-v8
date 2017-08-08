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
import cm.aptoide.pt.v8engine.billing.product.SimplePurchase;
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

  public Single<Boolean> isSupported(String applicationId, String type) {
    return billingService.getBilling(applicationId, type)
        .andThen(Single.just(true))
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof IllegalArgumentException) {
            return Single.just(false);
          }
          return Single.error(throwable);
        });
  }

  public Single<Product> getProduct(String applicationId, String productId) {
    return billingService.getProduct(applicationId, productId);
  }

  public Single<List<Product>> getProducts(String applicationId, List<String> productIds) {
    return billingService.getProducts(applicationId, productIds);
  }

  public Single<List<Purchase>> getPurchases(String applicationId) {
    return billingService.getPurchases(applicationId);
  }

  public Completable consumePurchase(String applicationId, String purchaseToken) {
    return getPurchases(applicationId).flatMapObservable(purchases -> Observable.from(purchases))
        .cast(InAppPurchase.class)
        .filter(purchase -> purchaseToken.equals(purchase.getToken()))
        .first()
        .toSingle()
        .flatMapCompletable(
            purchase -> getProduct(applicationId, purchase.getSku()).flatMapCompletable(
                product -> billingService.deletePurchase(applicationId, purchaseToken)
                    .andThen(transactionRepository.remove(product.getId()))));
  }

  public Single<List<PaymentMethod>> getPaymentMethods(String applicationId, String productId) {
    return getProduct(applicationId, productId).flatMap(
        product -> billingService.getPaymentMethods(product));
  }

  public Completable processPayment(String applicationId, String productId, String payload) {
    return getSelectedPaymentMethod(applicationId, productId).flatMap(
        paymentMethod -> getProduct(applicationId, productId).flatMap(
            product -> transactionRepository.createTransaction(paymentMethod.getId(), product,
                payload)))
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

  public Completable processLocalPayment(String applicationId, String productId, String payload,
      String localMetadata) {
    return getSelectedPaymentMethod(applicationId, productId).flatMap(
        paymentMethod -> getProduct(applicationId, productId).flatMap(
            product -> transactionRepository.createTransaction(paymentMethod.getId(), product,
                localMetadata, payload)))
        .toCompletable();
  }

  public Observable<Transaction> getTransaction(String applicationId, String productId) {
    return getProduct(applicationId, productId).flatMapObservable(
        product -> transactionRepository.getTransaction(product));
  }

  public Observable<Purchase> getPurchase(String applicationId, String productId) {
    return getTransaction(applicationId, productId).flatMapSingle(transaction -> {

      if (transaction.isPending() || transaction.isUnknown()) {
        return Single.just(new SimplePurchase(SimplePurchase.Status.PENDING));
      }

      if (transaction.isNew() || transaction.isFailed() || transaction.isPendingAuthorization()) {
        return Single.just(new SimplePurchase(SimplePurchase.Status.NEW));
      }

      return getProduct(applicationId, productId).flatMap(
          product -> billingService.getPurchase(product));
    })
        .flatMap(purchase -> {
          if (purchase.isFailed()) {
            return transactionRepository.remove(productId)
                .andThen(Observable.just(purchase));
          }
          return Observable.just(purchase);
        });
  }

  public Observable<Authorization> getAuthorization(String applicationId, String productId) {
    return getSelectedPaymentMethod(applicationId, productId).flatMapObservable(
        paymentMethod -> authorizationRepository.getAuthorization(paymentMethod.getId()));
  }

  public Completable authorize(String applicationId, String productId) {
    return getSelectedPaymentMethod(applicationId, productId).flatMap(
        paymentMethod -> authorizationRepository.createAuthorization(paymentMethod.getId()))
        .toCompletable();
  }

  public Completable selectPaymentMethod(int paymentMethodId, String applicationId,
      String productId) {
    return getPaymentMethod(paymentMethodId, applicationId, productId).flatMapCompletable(
        paymentMethod -> paymentMethodSelector.selectPaymentMethod(paymentMethod));
  }

  public Single<PaymentMethod> getSelectedPaymentMethod(String applicationId, String productId) {
    return getPaymentMethods(applicationId, productId).flatMap(
        paymentMethods -> paymentMethodSelector.selectedPaymentMethod(paymentMethods));
  }

  private Single<PaymentMethod> getPaymentMethod(int paymentMethodId, String applicationId,
      String productId) {
    return getPaymentMethods(applicationId, productId).flatMapObservable(
        payments -> Observable.from(payments)
            .filter(payment -> payment.getId() == paymentMethodId)
            .switchIfEmpty(Observable.error(
                new IllegalArgumentException("Payment " + paymentMethodId + " not found."))))
        .first()
        .toSingle();
  }
}