/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.inapp.InAppBillingBinder;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.InAppBillingRepository;
import cm.aptoide.pt.v8engine.billing.repository.PaymentRepositoryFactory;
import cm.aptoide.pt.v8engine.billing.repository.ProductRepositoryFactory;
import cm.aptoide.pt.v8engine.billing.services.PayPalPayment;
import cm.aptoide.pt.v8engine.billing.services.WebAuthorization;
import cm.aptoide.pt.v8engine.billing.services.WebPayment;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class AptoideBilling {

  private final ProductRepositoryFactory productRepositoryFactory;
  private final PaymentRepositoryFactory paymentRepositoryFactory;
  private final InAppBillingRepository inAppBillingRepository;
  private final AuthorizationRepository authorizationRepository;

  public AptoideBilling(ProductRepositoryFactory productRepositoryFactory,
      PaymentRepositoryFactory paymentRepositoryFactory,
      InAppBillingRepository inAppBillingRepository,
      AuthorizationRepository authorizationRepository) {
    this.productRepositoryFactory = productRepositoryFactory;
    this.paymentRepositoryFactory = paymentRepositoryFactory;
    this.inAppBillingRepository = inAppBillingRepository;
    this.authorizationRepository = authorizationRepository;
  }

  public Single<Boolean> isBillingSupported(String packageName, int apiVersion, String type) {
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

  public Single<List<Payment>> getPayments(Product product) {
    return productRepositoryFactory.getProductRepository(product)
        .getPayments(product);
  }

  public Observable<WebAuthorization> getWebPaymentAuthorization(int paymentId, Product product) {
    return getWebPayment(paymentId, product).flatMapObservable(
        payment -> payment.getAuthorization());
  }

  public Completable processWebPayment(int paymentId, Product product) {
    return getWebPayment(paymentId, product).flatMapCompletable(
        payment -> payment.process(product));
  }

  public Completable processPayPalPayment(Product product, String authorizationCode) {
    return getPayments(product).flatMapObservable(payments -> Observable.from(payments))
        .filter(payment -> payment instanceof PayPalPayment)
        .first()
        .cast(PayPalPayment.class)
        .toSingle()
        .flatMapCompletable(payment -> payment.process(product, authorizationCode));
  }

  public Observable<PaymentConfirmation> getConfirmation(Product product) {
    return paymentRepositoryFactory.getPaymentConfirmationRepository(product)
        .getPaymentConfirmation(product)
        .distinctUntilChanged(confirmation -> confirmation.getStatus());
  }

  public Single<Purchase> getPurchase(Product product) {
    return productRepositoryFactory.getProductRepository(product)
        .getPurchase(product);
  }

  private Single<WebPayment> getWebPayment(int paymentId, Product product) {
    return getPayments(product).flatMapObservable(payments -> Observable.from(payments)
        .filter(payment -> payment.getId() == paymentId)
        .switchIfEmpty(Observable.error(
            new PaymentFailureException("Payment " + paymentId + "not available"))))
        .first()
        .cast(WebPayment.class)
        .toSingle();
  }
}