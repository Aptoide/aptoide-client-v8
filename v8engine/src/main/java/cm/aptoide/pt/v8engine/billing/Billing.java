/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.v8engine.billing.authorization.Authorization;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentMethodNotAuthorizedException;
import cm.aptoide.pt.v8engine.billing.inapp.InAppBillingBinder;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.InAppBillingRepository;
import cm.aptoide.pt.v8engine.billing.repository.ProductRepositoryFactory;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepository;
import cm.aptoide.pt.v8engine.billing.transaction.Transaction;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class Billing {

  private final ProductRepositoryFactory productRepositoryFactory;
  private final TransactionRepository transactionRepository;
  private final InAppBillingRepository inAppBillingRepository;
  private final AuthorizationRepository authorizationRepository;
  private final PaymentMethodSelector paymentMethodSelector;

  public Billing(ProductRepositoryFactory productRepositoryFactory,
      TransactionRepository transactionRepository, InAppBillingRepository inAppBillingRepository,
      AuthorizationRepository authorizationRepository,
      PaymentMethodSelector paymentMethodSelector) {
    this.productRepositoryFactory = productRepositoryFactory;
    this.transactionRepository = transactionRepository;
    this.inAppBillingRepository = inAppBillingRepository;
    this.authorizationRepository = authorizationRepository;
    this.paymentMethodSelector = paymentMethodSelector;
  }

  public Single<Boolean> isSupported(String packageName, int apiVersion, String type) {
    return inAppBillingRepository.getInAppBilling(apiVersion, packageName, type)
        .map(billing -> true)
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryItemNotFoundException) {
            return Observable.just(false);
          }
          return Observable.error(throwable);
        })
        .first()
        .toSingle();
  }

  public Single<Product> getPaidAppProduct(long appId, String storeName, boolean sponsored) {
    return productRepositoryFactory.getPaidAppProductRepository()
        .getProduct(appId, sponsored, storeName);
  }

  public Single<List<Product>> getInAppProducts(int apiVersion, String packageName,
      List<String> skus, String type) {
    return productRepositoryFactory.getInAppProductRepository()
        .getProducts(apiVersion, packageName, skus, type);
  }

  public Single<Product> getInAppProduct(int apiVersion, String packageName, String sku,
      String type, String developerPayload) {
    return productRepositoryFactory.getInAppProductRepository()
        .getProduct(apiVersion, packageName, sku, type, developerPayload);
  }

  public Single<List<Purchase>> getInAppPurchases(int apiVersion, String packageName, String type) {
    return productRepositoryFactory.getInAppProductRepository()
        .getPurchases(apiVersion, packageName, type);
  }

  public Completable consumeInAppPurchase(int apiVersion, String packageName,
      String purchaseToken) {
    return productRepositoryFactory.getInAppProductRepository()
        .getPurchase(apiVersion, packageName, purchaseToken, InAppBillingBinder.ITEM_TYPE_INAPP)
        .flatMapCompletable(purchase -> purchase.consume());
  }

  public Single<List<PaymentMethod>> getPaymentMethods(Product product) {
    return productRepositoryFactory.getProductRepository(product)
        .getPaymentMethods(product);
  }

  public Completable processPayment(int paymentId, Product product) {
    return transactionRepository.createTransaction(paymentId, product)
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
    return transactionRepository.getTransaction(product)
        .distinctUntilChanged(transaction -> transaction.getStatus());
  }

  public Single<Purchase> getPurchase(Product product) {
    return productRepositoryFactory.getProductRepository(product)
        .getPurchase(product)
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryIllegalArgumentException) {
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

  private Single<PaymentMethod> getPaymentMethod(int paymentId, Product product) {
    return getPaymentMethods(product).flatMapObservable(payments -> Observable.from(payments)
        .filter(payment -> payment.getId() == paymentId)
        .switchIfEmpty(
            Observable.error(new PaymentFailureException("Payment " + paymentId + " not found."))))
        .first()
        .toSingle();
  }
}