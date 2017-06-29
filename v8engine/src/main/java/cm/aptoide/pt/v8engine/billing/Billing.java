/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.inapp.InAppBillingBinder;
import cm.aptoide.pt.v8engine.billing.repository.InAppBillingRepository;
import cm.aptoide.pt.v8engine.billing.repository.ProductRepositoryFactory;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepositoryFactory;
import cm.aptoide.pt.v8engine.billing.methods.BoaCompraAuthorization;
import cm.aptoide.pt.v8engine.billing.methods.BoaCompraPaymentMethod;
import cm.aptoide.pt.v8engine.billing.methods.PayPalPaymentMethod;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class Billing {

  private final ProductRepositoryFactory productRepositoryFactory;
  private final TransactionRepositoryFactory transactionRepositoryFactory;
  private final InAppBillingRepository inAppBillingRepository;

  public Billing(ProductRepositoryFactory productRepositoryFactory,
      TransactionRepositoryFactory transactionRepositoryFactory,
      InAppBillingRepository inAppBillingRepository) {
    this.productRepositoryFactory = productRepositoryFactory;
    this.transactionRepositoryFactory = transactionRepositoryFactory;
    this.inAppBillingRepository = inAppBillingRepository;
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

  public Completable processBoaCompraPayment(int paymentId, Product product) {
    return getPaymentMethods(paymentId, product).flatMapCompletable(
        payment -> ((BoaCompraPaymentMethod) payment).authorizedProcess(product));
  }

  public Single<BoaCompraAuthorization> getInitializedBoaCompraAuthorization(int paymentId,
      Product product) {
    return getPaymentMethods(paymentId, product).flatMap(
        payment -> ((BoaCompraPaymentMethod) payment).getInitializedAuthorization());
  }

  public Completable processPayment(int paymentId, Product product) {
    return getPaymentMethods(paymentId, product).flatMapCompletable(
        payment -> payment.process(product));
  }

  public Completable processPayPalPayment(Product product, String payPalConfirmationId) {
    return getPaymentMethods(product).flatMapObservable(payments -> Observable.from(payments))
        .filter(payment -> payment instanceof PayPalPaymentMethod)
        .first()
        .cast(PayPalPaymentMethod.class)
        .toSingle()
        .flatMapCompletable(payment -> payment.process(product, payPalConfirmationId));
  }

  public Observable<Transaction> getTransaction(Product product) {
    return transactionRepositoryFactory.getTransactionRepository(product)
        .getTransaction(product)
        .distinctUntilChanged(confirmation -> confirmation.getStatus());
  }

  public Single<Purchase> getPurchase(Product product) {
    return productRepositoryFactory.getProductRepository(product)
        .getPurchase(product)
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryIllegalArgumentException) {
            return transactionRepositoryFactory.getTransactionRepository(product)
                .remove(product.getId())
                .andThen(Single.error(throwable));
          }
          return Single.error(throwable);
        });
  }

  public Single<PaymentMethod> getPaymentMethods(int paymentId, Product product) {
    return getPaymentMethods(product).flatMapObservable(payments -> Observable.from(payments)
        .filter(payment -> payment.getId() == paymentId)
        .switchIfEmpty(Observable.error(
            new PaymentFailureException("Payment " + paymentId + " not available"))))
        .first()
        .toSingle();
  }
}