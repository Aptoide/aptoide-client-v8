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

  public Single<Merchant> getMerchant(String merchantName) {
    return billingService.getMerchant(merchantName);
  }

  public Single<Product> getProduct(String merchantName, String productId) {
    return billingService.getProduct(merchantName, productId);
  }

  public Single<List<Product>> getProducts(String merchantName, List<String> productIds) {
    return billingService.getProducts(merchantName, productIds);
  }

  public Single<List<Purchase>> getPurchases(String merchantName) {
    return billingService.getPurchases(merchantName);
  }

  public Completable consumePurchase(String merchantName, String purchaseToken) {
    return billingService.getPurchase(merchantName, purchaseToken)
        .flatMapCompletable(purchase -> billingService.deletePurchase(merchantName, purchaseToken)
            .andThen(transactionRepository.remove(purchase.getProductId(), merchantName)));
  }

  public Single<List<PaymentMethod>> getPaymentMethods() {
    return billingService.getPaymentMethods();
  }

  public Completable processPayment(String merchantName, String productId, String payload) {
    return getSelectedPaymentMethod().flatMap(
        paymentMethod -> getProduct(merchantName, productId).flatMap(
            product -> transactionRepository.createTransaction(merchantName, paymentMethod.getId(),
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

  public Completable processLocalPayment(String merchantName, String productId, String payload,
      String localMetadata) {
    return getSelectedPaymentMethod().flatMap(
        paymentMethod -> getProduct(merchantName, productId).flatMap(
            product -> transactionRepository.createTransaction(merchantName, paymentMethod.getId(),
                product, localMetadata, payload)))
        .toCompletable();
  }

  public Observable<Transaction> getTransaction(String merchantName, String productId) {
    return getProduct(merchantName, productId).flatMapObservable(
        product -> transactionRepository.getTransaction(product, merchantName));
  }

  public Observable<Purchase> getPurchase(String merchantName, String productId) {
    return getTransaction(merchantName, productId).flatMapSingle(transaction -> {

      if (transaction.isPending() || transaction.isUnknown()) {
        return Single.just(new SimplePurchase(SimplePurchase.Status.PENDING, productId));
      }

      if (transaction.isNew() || transaction.isFailed() || transaction.isPendingAuthorization()) {
        return Single.just(new SimplePurchase(SimplePurchase.Status.NEW, productId));
      }

      return getProduct(merchantName, productId).flatMap(
          product -> billingService.getPurchase(product));
    })
        .flatMap(purchase -> {
          if (purchase.isFailed()) {
            return transactionRepository.remove(productId, merchantName)
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
    return getPaymentMethods().flatMapObservable(payments -> Observable.from(payments)
        .filter(payment -> payment.getId() == paymentMethodId)
        .switchIfEmpty(Observable.error(
            new IllegalArgumentException("Payment " + paymentMethodId + " not found."))))
        .first()
        .toSingle();
  }
}