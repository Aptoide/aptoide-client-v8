/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationRepository;
import cm.aptoide.pt.billing.exception.PaymentFailureException;
import cm.aptoide.pt.billing.exception.ServiceNotAuthorizedException;
import cm.aptoide.pt.billing.payment.AdyenPaymentService;
import cm.aptoide.pt.billing.payment.Payment;
import cm.aptoide.pt.billing.payment.PaymentService;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.transaction.AuthorizedTransaction;
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

  public Billing(String merchantName, BillingService billingService,
      TransactionRepository transactionRepository, AuthorizationRepository authorizationRepository,
      PaymentServiceSelector paymentServiceSelector, Customer customer,
      PurchaseTokenDecoder tokenDecoder, BillingSyncScheduler syncScheduler) {
    this.transactionRepository = transactionRepository;
    this.billingService = billingService;
    this.authorizationRepository = authorizationRepository;
    this.paymentServiceSelector = paymentServiceSelector;
    this.customer = customer;
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

  public Observable<Payment> getPayment(String sku) {
    return getPaymentServices().flatMapObservable(services -> getProduct(sku).flatMapObservable(
        product -> getAuthorizedTransaction(product).switchMap(
            authorizedTransaction -> Observable.combineLatest(getSelectedService(),
                getPurchase(product),
                (paymentService, purchase) -> new Payment(product, paymentService,
                    authorizedTransaction, purchase, services)))));
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

  public Completable processPayment(String sku, String payload) {
    return getPayment(sku).first()
        .toSingle()
        .flatMap(payment -> {
          if (payment.getSelectedPaymentService() instanceof AdyenPaymentService) {
            return ((AdyenPaymentService) payment.getSelectedPaymentService()).getToken()
                .flatMap(token -> transactionRepository.createTransaction(payment.getProduct()
                    .getId(), payment.getSelectedPaymentService()
                    .getId(), payload, token));
          }
          return transactionRepository.createTransaction(payment.getProduct()
              .getId(), payment.getSelectedPaymentService()
              .getId(), payload);
        })
        .flatMapCompletable(
            transaction -> removeOldTransactions(transaction).andThen(Completable.defer(() -> {
              if (transaction.isPendingAuthorization()) {
                return Completable.error(
                    new ServiceNotAuthorizedException("Pending service authorization."));
              }

              if (transaction.isFailed()) {
                return Completable.error(new PaymentFailureException("Payment failed."));
              }

              return Completable.complete();
            })));
  }

  public Completable authorize(String sku, String metadata) {
    return getPayment(sku).first()
        .map(payment -> payment.getTransaction())
        .cast(AuthorizedTransaction.class)
        .toSingle()
        .flatMapCompletable(authorizedTransaction -> authorizationRepository.updateAuthorization(
            authorizedTransaction.getAuthorization()
                .getId(), metadata, Authorization.Status.PENDING_SYNC));
  }

  public Completable selectService(String serviceId) {
    return getService(serviceId).flatMapCompletable(
        service -> paymentServiceSelector.selectService(service))
        .onErrorComplete();
  }

  public void stopSync() {
    syncScheduler.stopSyncs();
  }

  private Observable<Purchase> getPurchase(Product product) {
    return billingService.getPurchase(product.getId())
        .toObservable();
  }

  private Single<Product> getProduct(String sku) {
    return billingService.getProduct(sku, merchantName);
  }

  private Observable<Authorization> getAuthorization(Transaction transaction) {
    if (transaction.isNew()) {
      return authorizationRepository.createAuthorization(transaction.getId(),
          Authorization.Status.NEW)
          .flatMapObservable(__ -> authorizationRepository.getAuthorization(transaction.getId()));
    }
    return authorizationRepository.getAuthorization(transaction.getId());
  }

  private Observable<AuthorizedTransaction> getAuthorizedTransaction(Product product) {
    return transactionRepository.getTransaction(product.getId())
        .switchMap(transaction -> getAuthorization(transaction).map(
            authorization -> new AuthorizedTransaction(transaction, authorization)));
  }

  private Observable<PaymentService> getSelectedService() {
    return getPaymentServices().flatMapObservable(
        services -> paymentServiceSelector.getSelectedService(services));
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

  private Single<List<PaymentService>> getPaymentServices() {
    return billingService.getPaymentServices();
  }

  private Completable removeOldTransactions(Transaction transaction) {
    return transactionRepository.getOtherTransactions(transaction.getCustomerId(),
        transaction.getProductId(), transaction.getId())
        .flatMapObservable(otherTransactions -> Observable.from(otherTransactions))
        .flatMapCompletable(
            otherTransaction -> transactionRepository.removeTransaction(otherTransaction.getId())
                .andThen(authorizationRepository.removeAuthorization(otherTransaction.getId())))
        .toCompletable();
  }
}
