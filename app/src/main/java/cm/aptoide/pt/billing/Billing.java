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
  private final PaymentServiceSelector paymentServiceSelector;
  private final Customer customer;
  private final AuthorizationFactory authorizationFactory;
  private final PurchaseTokenDecoder tokenDecoder;
  private final String merchantName;
  private final BillingSyncScheduler syncScheduler;

  public Billing(String merchantName, BillingService billingService,
      TransactionRepository transactionRepository, AuthorizationRepository authorizationRepository,
      PaymentServiceSelector paymentServiceSelector, Customer customer,
      AuthorizationFactory authorizationFactory, PurchaseTokenDecoder tokenDecoder,
      BillingSyncScheduler syncScheduler) {
    this.transactionRepository = transactionRepository;
    this.billingService = billingService;
    this.authorizationRepository = authorizationRepository;
    this.paymentServiceSelector = paymentServiceSelector;
    this.customer = customer;
    this.authorizationFactory = authorizationFactory;
    this.tokenDecoder = tokenDecoder;
    this.merchantName = merchantName;
    this.syncScheduler = syncScheduler;
  }

  public Customer getCustomer() {
    return customer;
  }

  public Single<Merchant> getMerchant() {
    return billingService.getMerchant(merchantName);
  }

  public Single<Product> getProduct(String sku) {
    return billingService.getProduct(sku, merchantName);
  }

  public Single<List<Product>> getProducts(List<String> skus) {
    return billingService.getProducts(merchantName, skus);
  }

  public Single<List<Purchase>> getPurchases() {
    return billingService.getPurchases(merchantName);
  }

  public Completable consumePurchase(String purchaseToken) {
    return billingService.getPurchase(tokenDecoder.
        decode(purchaseToken))
        .flatMapCompletable(
            purchase -> billingService.deletePurchase(tokenDecoder.decode(purchaseToken))
                .andThen(transactionRepository.remove(purchase.getProductId())));
  }

  public Single<List<PaymentService>> getPaymentServices() {
    return billingService.getPaymentServices();
  }

  public Completable processPayment(String sku, String payload) {
    return getSelectedService().flatMap(service -> getProduct(sku).flatMap(
        product -> transactionRepository.createTransaction(product.getProductId(), service.getId(),
            payload)))
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

  public Completable authorize(String sku, String metadata) {
    return getAuthorization(sku).first()
        .cast(PayPalAuthorization.class)
        .toSingle()
        .flatMapCompletable(authorization -> getSelectedService().flatMapCompletable(
            service -> authorizationRepository.updateAuthorization(
                authorizationFactory.create(authorization.getId(), authorization.getCustomerId(),
                    AuthorizationFactory.PAYPAL_SDK, Authorization.Status.PENDING_SYNC, null, null,
                    metadata, authorization.getPrice(), authorization.getDescription(),
                    authorization.getTransactionId()))));
  }

  public Observable<Authorization> getAuthorization(String sku) {
    return getTransaction(sku).first()
        .toSingle()
        .flatMapObservable(
            transaction -> authorizationRepository.getAuthorization(transaction.getId()));
  }

  public Observable<Purchase> getPurchase(String sku) {
    return getTransaction(sku).flatMapSingle(transaction -> {
      if (transaction.isProcessing()) {
        return Single.just(
            new SimplePurchase(SimplePurchase.Status.PENDING, transaction.getProductId()));
      }

      if (transaction.isNew() || transaction.isFailed() || transaction.isPendingAuthorization()) {
        return Single.just(
            new SimplePurchase(SimplePurchase.Status.NEW, transaction.getProductId()));
      }

      return billingService.getProductPurchase(transaction.getProductId());
    })
        .flatMap(purchase -> {
          if (purchase.isFailed()) {
            return transactionRepository.remove(purchase.getProductId())
                .andThen(Observable.just(purchase));
          }
          return Observable.just(purchase);
        });
  }

  public Completable selectService(long serviceId) {
    return getService(serviceId).flatMapCompletable(
        service -> paymentServiceSelector.selectService(service));
  }

  public Single<PaymentService> getSelectedService() {
    return getPaymentServices().flatMap(
        services -> paymentServiceSelector.selectedService(services));
  }

  private Observable<Transaction> getTransaction(String sku) {
    return getProduct(sku).flatMapObservable(
        product -> transactionRepository.getTransaction(product.getProductId()));
  }

  private Single<PaymentService> getService(long serviceId) {
    return getPaymentServices().flatMapObservable(payments -> Observable.from(payments)
        .filter(payment -> payment.getId() == serviceId)
        .switchIfEmpty(
            Observable.error(new IllegalArgumentException("Payment " + serviceId + " not found."))))
        .first()
        .toSingle();
  }

  public void stopSync() {
    syncScheduler.stopSyncs();
  }
}