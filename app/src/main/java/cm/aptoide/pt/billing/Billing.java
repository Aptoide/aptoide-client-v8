/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationRepository;
import cm.aptoide.pt.billing.exception.PaymentFailureException;
import cm.aptoide.pt.billing.exception.PaymentMethodNotAuthorizedException;
import cm.aptoide.pt.billing.product.SimplePurchase;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionRepository;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class Billing {

  private final TransactionRepository transactionRepository;
  private final BillingService billingService;
  private final AuthorizationRepository authorizationRepository;
  private final PaymentMethodSelector paymentMethodSelector;
  private final Customer customer;

  public Billing(TransactionRepository transactionRepository, BillingService billingService,
      AuthorizationRepository authorizationRepository, PaymentMethodSelector paymentMethodSelector,
      Customer customer) {
    this.transactionRepository = transactionRepository;
    this.billingService = billingService;
    this.authorizationRepository = authorizationRepository;
    this.paymentMethodSelector = paymentMethodSelector;
    this.customer = customer;
  }

  public Customer getCustomer() {
    return customer;
  }

  public Single<Boolean> isSupported(String sellerId, String type) {
    return billingService.getBilling(sellerId, type)
        .andThen(Single.just(true))
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof IllegalArgumentException) {
            return Single.just(false);
          }
          return Single.error(throwable);
        });
  }

  public Single<Product> getProduct(String sellerId, String productId) {
    return billingService.getProduct(sellerId, productId);
  }

  public Single<List<Product>> getProducts(String sellerId, List<String> productIds) {
    return billingService.getProducts(sellerId, productIds);
  }

  public Single<List<Purchase>> getPurchases(String sellerId) {
    return billingService.getPurchases(sellerId);
  }

  public Completable consumePurchase(String sellerId, String purchaseToken) {
    return billingService.getPurchase(sellerId, purchaseToken)
        .flatMapCompletable(purchase -> billingService.deletePurchase(sellerId, purchaseToken)
            .andThen(transactionRepository.remove(purchase.getProductId(), sellerId)));
  }

  public Single<List<PaymentMethod>> getPaymentMethods() {
    return billingService.getPaymentMethods();
  }

  public Completable processPayment(String sellerId, String productId, String payload) {
    return getSelectedPaymentMethod().flatMap(
        paymentMethod -> getProduct(sellerId, productId).flatMap(
            product -> transactionRepository.createTransaction(sellerId, paymentMethod.getId(),
                product, payload)))
        .flatMapCompletable(transaction -> {
          if (transaction.isPendingAuthorization()) {
            return authorizationRepository.createAuthorization(transaction.getPaymentMethodId(),
                Authorization.Status.INACTIVE)
                .flatMapCompletable(authorization -> Completable.error(
                    new PaymentMethodNotAuthorizedException(
                        "Pending payment method authorization.")));
          }

          if (transaction.isFailed()) {
            return Completable.error(new PaymentFailureException("Payment failed."));
          }

          return Completable.complete();
        });
  }

  public Completable processLocalPayment(String sellerId, String productId, String payload,
      String localMetadata) {
    return getSelectedPaymentMethod().flatMap(
        paymentMethod -> getProduct(sellerId, productId).flatMap(
            product -> transactionRepository.createTransaction(sellerId, paymentMethod.getId(),
                product, localMetadata, payload)))
        .toCompletable();
  }

  public Observable<Transaction> getTransaction(String sellerId, String productId) {
    return getProduct(sellerId, productId).flatMapObservable(
        product -> transactionRepository.getTransaction(product, sellerId));
  }

  public Observable<Purchase> getPurchase(String sellerId, String productId) {
    return getTransaction(sellerId, productId).flatMapSingle(transaction -> {

      if (transaction.isPending() || transaction.isUnknown()) {
        return Single.just(new SimplePurchase(SimplePurchase.Status.PENDING, productId));
      }

      if (transaction.isNew() || transaction.isFailed() || transaction.isPendingAuthorization()) {
        return Single.just(new SimplePurchase(SimplePurchase.Status.NEW, productId));
      }

      return getProduct(sellerId, productId).flatMap(
          product -> billingService.getPurchase(product));
    })
        .flatMap(purchase -> {
          if (purchase.isFailed()) {
            return transactionRepository.remove(productId, sellerId)
                .andThen(Observable.just(purchase));
          }
          return Observable.just(purchase);
        });
  }

  public Observable<Authorization> getAuthorization() {
    return getSelectedPaymentMethod().flatMapObservable(
        paymentMethod -> authorizationRepository.getAuthorization(paymentMethod.getId()));
  }

  public Completable authorize() {
    return getSelectedPaymentMethod().flatMap(
        paymentMethod -> authorizationRepository.createAuthorization(paymentMethod.getId()))
        .toCompletable();
  }

  public Completable selectPaymentMethod(int paymentMethodId) {
    return getPaymentMethod(paymentMethodId).flatMapCompletable(
        paymentMethod -> paymentMethodSelector.selectPaymentMethod(paymentMethod));
  }

  public Single<PaymentMethod> getSelectedPaymentMethod() {
    return getPaymentMethods().flatMap(
        paymentMethods -> paymentMethodSelector.selectedPaymentMethod(paymentMethods));
  }

  private Single<PaymentMethod> getPaymentMethod(int paymentMethodId) {
    return getPaymentMethods().flatMapObservable(
        payments -> Observable.from(payments)
            .filter(payment -> payment.getId() == paymentMethodId)
            .switchIfEmpty(Observable.error(
                new IllegalArgumentException("Payment " + paymentMethodId + " not found."))))
        .first()
        .toSingle();
  }
}