/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import android.content.Context;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.inapp.BillingBinder;
import cm.aptoide.pt.v8engine.billing.repository.InAppBillingRepository;
import cm.aptoide.pt.v8engine.billing.repository.PaymentRepositoryFactory;
import cm.aptoide.pt.v8engine.billing.repository.ProductRepositoryFactory;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class AptoideBilling {

  private final ProductRepositoryFactory productRepositoryFactory;
  private final PaymentRepositoryFactory paymentRepositoryFactory;
  private final InAppBillingRepository inAppBillingRepository;

  public AptoideBilling(ProductRepositoryFactory productRepositoryFactory,
      PaymentRepositoryFactory paymentRepositoryFactory,
      InAppBillingRepository inAppBillingRepository) {
    this.productRepositoryFactory = productRepositoryFactory;
    this.paymentRepositoryFactory = paymentRepositoryFactory;
    this.inAppBillingRepository = inAppBillingRepository;
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
        .getPurchase(apiVersion, packageName, purchaseToken, BillingBinder.ITEM_TYPE_INAPP)
        .flatMapCompletable(purchase -> purchase.consume());
  }

  public Single<List<Payment>> getPayments(Context context, Product product) {
    return productRepositoryFactory.getProductRepository(product)
        .getPayments(context, product);
  }

  public Single<Payment> getPayment(Context context, int paymentId, Product product) {
    return getPayments(context, product).flatMapObservable(payments -> Observable.from(payments)
        .filter(payment -> payment.getId() == paymentId)
        .switchIfEmpty(Observable.error(
            new PaymentFailureException("Payment " + paymentId + "not available"))))
        .toSingle();
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
}