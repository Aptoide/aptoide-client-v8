/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.AuthorizationRepository;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.billing.exception.PaymentFailureException;
import cm.aptoide.pt.billing.exception.ServiceNotAuthorizedException;
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
  private final AuthorizationFactory authorizationFactory;

  public Billing(TransactionRepository transactionRepository, BillingService billingService,
      AuthorizationRepository authorizationRepository, PaymentMethodSelector paymentMethodSelector,
      Customer customer, AuthorizationFactory authorizationFactory) {
    this.transactionRepository = transactionRepository;
    this.billingService = billingService;
    this.authorizationRepository = authorizationRepository;
    this.paymentMethodSelector = paymentMethodSelector;
    this.customer = customer;
    this.authorizationFactory = authorizationFactory;
  }

  public Customer getCustomer() {
    return customer;
  }

  public Single<Merchant> getMerchant(String merchantName) {
    return billingService.getMerchant(merchantName);
  }

  public Single<Product> getProduct(String merchantName, String sku) {
    return billingService.getProduct(merchantName, sku);
  }

  public Single<List<Product>> getProducts(String merchantName, List<String> skus) {
    return billingService.getProducts(merchantName, skus);
  }

  public Single<List<Purchase>> getPurchases(String merchantName) {
    return billingService.getPurchases(merchantName);
  }

  public Completable consumePurchase(String merchantName, String purchaseToken) {
    return billingService.getPurchase(merchantName, purchaseToken)
        .flatMapCompletable(purchase -> billingService.deletePurchase(merchantName, purchaseToken)
            .andThen(transactionRepository.remove(purchase.getProductId())));
  }

  public Single<List<PaymentMethod>> getPaymentMethods() {
    return billingService.getPaymentMethods();
  }

  public Completable processPayment(String merchantName, String sku, String payload) {
    return getSelectedPaymentMethod().flatMap(
        paymentMethod -> getProduct(merchantName, sku).flatMap(
            product -> transactionRepository.createTransaction(product.getProductId(),
                paymentMethod.getId(), payload)))
        .flatMapCompletable(transaction -> {
          if (transaction.isPendingAuthorization()) {
            return Completable.error(
                new ServiceNotAuthorizedException("Pending service authorization."));
          }

          if (transaction.isFailed()) {
            return Completable.error(new PaymentFailureException("Payment failed."));
          }

          return Completable.complete();
        });
  }

  public Completable authorize(String merchantName, String sku, String metadata) {
    return getAuthorization(merchantName, sku).first()
        .cast(PayPalAuthorization.class)
        .toSingle()
        .flatMapCompletable(authorization -> getSelectedPaymentMethod().flatMapCompletable(
            service -> authorizationRepository.updateAuthorization(
                authorizationFactory.create(authorization.getId(), authorization.getCustomerId(),
                    AuthorizationFactory.PAYPAL_SDK, Authorization.Status.PENDING_SYNC, null, null,
                    metadata, authorization.getPrice(), authorization.getDescription(),
                    authorization.getTransactionId()))));
  }

  public Observable<Authorization> getAuthorization(String merchantName, String sku) {
    return getTransaction(merchantName, sku).first()
        .toSingle()
        .flatMapObservable(
            transaction -> authorizationRepository.getAuthorization(transaction.getId()));
  }

  public Observable<Purchase> getPurchase(String merchantName, String sku) {
    return getTransaction(merchantName, sku).flatMapSingle(transaction -> {

      if (transaction.isPending() || transaction.isUnknown()) {
        return Single.just(
            new SimplePurchase(SimplePurchase.Status.PENDING, transaction.getProductId()));
      }

      if (transaction.isNew() || transaction.isFailed() || transaction.isPendingAuthorization()) {
        return Single.just(
            new SimplePurchase(SimplePurchase.Status.NEW, transaction.getProductId()));
      }

      return billingService.getPurchase(merchantName, sku);
    })
        .flatMap(purchase -> {
          if (purchase.isFailed()) {
            return transactionRepository.remove(purchase.getProductId())
                .andThen(Observable.just(purchase));
          }
          return Observable.just(purchase);
        });
  }

  public Completable selectPaymentMethod(int paymentMethodId) {
    return getPaymentMethod(paymentMethodId).flatMapCompletable(
        paymentMethod -> paymentMethodSelector.selectPaymentMethod(paymentMethod));
  }

  public Single<PaymentMethod> getSelectedPaymentMethod() {
    return getPaymentMethods().flatMap(
        paymentMethods -> paymentMethodSelector.selectedPaymentMethod(paymentMethods));
  }

  private Observable<Transaction> getTransaction(String merchantName, String sku) {
    return getProduct(merchantName, sku).flatMapObservable(
        product -> transactionRepository.getTransaction(product.getProductId()));
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