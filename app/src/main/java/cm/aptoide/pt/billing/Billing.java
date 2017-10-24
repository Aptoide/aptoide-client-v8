/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationRepository;
import cm.aptoide.pt.billing.exception.PaymentFailureException;
import cm.aptoide.pt.billing.exception.ServiceNotAuthorizedException;
import cm.aptoide.pt.billing.payment.PaymentService;
import cm.aptoide.pt.billing.payment.PaymentServiceSelector;
import cm.aptoide.pt.billing.payment.AdyenPaymentService;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.purchase.PurchaseFactory;
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
  private final PurchaseTokenDecoder tokenDecoder;
  private final String merchantName;
  private final BillingSyncScheduler syncScheduler;
  private final PurchaseFactory purchaseFactory;

  public Billing(String merchantName, BillingService billingService,
      TransactionRepository transactionRepository, AuthorizationRepository authorizationRepository,
      PaymentServiceSelector paymentServiceSelector, Customer customer,
      PurchaseTokenDecoder tokenDecoder, BillingSyncScheduler syncScheduler,
      PurchaseFactory purchaseFactory) {
    this.transactionRepository = transactionRepository;
    this.billingService = billingService;
    this.authorizationRepository = authorizationRepository;
    this.paymentServiceSelector = paymentServiceSelector;
    this.customer = customer;
    this.tokenDecoder = tokenDecoder;
    this.merchantName = merchantName;
    this.syncScheduler = syncScheduler;
    this.purchaseFactory = purchaseFactory;
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
    return billingService.deletePurchase(tokenDecoder.decode(purchaseToken));
  }

  public Single<List<PaymentService>> getPaymentServices() {
    return billingService.getPaymentServices();
  }

  public Completable processPayment(String sku, String payload) {
    return getSelectedService().flatMap(service -> getProduct(sku).flatMap(product -> {

      if (service instanceof AdyenPaymentService) {
        return ((AdyenPaymentService) service).getToken()
            .flatMap(token -> transactionRepository.createTransaction(product.getProductId(),
                service.getId(), payload, token));
      }
      return transactionRepository.createTransaction(product.getProductId(), service.getId(),
          payload);
    }))
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
        .toSingle()
        .flatMapCompletable(authorization -> authorizationRepository.updateAuthorization(
            authorization.getTransactionId(), metadata, Authorization.Status.PENDING_SYNC));
  }

  public Observable<Authorization> getAuthorization(String sku) {
    return getTransaction(sku).first()
        .toSingle()
        .flatMapObservable(transaction -> {
          if (transaction.isNew()) {
            return authorizationRepository.createAuthorization(transaction.getId(),
                Authorization.Status.NEW)
                .flatMapObservable(
                    __ -> authorizationRepository.getAuthorization(transaction.getId()));
          }
          return authorizationRepository.getAuthorization(transaction.getId());
        });
  }

  public Observable<Purchase> getPurchase(String sku) {
    return Observable.combineLatest(getTransaction(sku), getAuthorization(sku),
        (transaction, authorization) -> {

          if (transaction.isCompleted()) {
            return billingService.getPurchase(transaction.getProductId())
                .map(purchase -> {
                  if (purchase.isFailed()) {
                    return purchaseFactory.create(purchase.getProductId(), null, null,
                        Purchase.Status.NEW, sku, null, null, transaction.getId());
                  }
                  return purchase;
                });
          }

          if (transaction.isProcessing()
              || authorization.isRedeemed()
              || authorization.isProcessing()) {
            return Single.just(purchaseFactory.create(transaction.getProductId(), null, null,
                Purchase.Status.PENDING, sku, null, null, transaction.getId()));
          }

          if (transaction.isFailed() || authorization.isFailed()) {
            return Single.just(purchaseFactory.create(transaction.getProductId(), null, null,
                Purchase.Status.FAILED, sku, null, null, transaction.getId()));
          }

          return Single.just(
              purchaseFactory.create(transaction.getProductId(), null, null, Purchase.Status.NEW,
                  sku, null, null, transaction.getId()));
        })
        .flatMapSingle(single -> single);
  }

  public Completable selectService(String serviceId) {
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

  private Single<PaymentService> getService(String serviceId) {
    return getPaymentServices().flatMapObservable(payments -> Observable.from(payments)
        .filter(payment -> payment.getId()
            .equals(serviceId))
        .switchIfEmpty(
            Observable.error(new IllegalArgumentException("Payment " + serviceId + " not found."))))
        .first()
        .toSingle();
  }

  public void stopSync() {
    syncScheduler.stopSyncs();
  }
}