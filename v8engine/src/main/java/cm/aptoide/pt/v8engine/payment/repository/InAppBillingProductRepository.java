/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.repository;

import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.ProductRepository;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.PurchaseFactory;
import cm.aptoide.pt.v8engine.payment.products.InAppBillingProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class InAppBillingProductRepository implements ProductRepository {

  private final InAppBillingRepository inAppBillingRepository;
  private final PurchaseFactory purchaseFactory;
  private final PaymentFactory paymentFactory;
  private final InAppBillingProduct product;

  public InAppBillingProductRepository(InAppBillingRepository inAppBillingRepository,
      PurchaseFactory purchaseFactory, PaymentFactory paymentFactory, InAppBillingProduct product) {
    this.inAppBillingRepository = inAppBillingRepository;
    this.purchaseFactory = purchaseFactory;
    this.paymentFactory = paymentFactory;
    this.product = product;
  }

  @Override public Single<Purchase> getPurchase(Product product) {
    return inAppBillingRepository.getInAppPurchaseInformation(
        ((InAppBillingProduct) product).getApiVersion(),
        ((InAppBillingProduct) product).getPackageName(), ((InAppBillingProduct) product).getType())
        .flatMap(purchaseInformation -> convertToPurchase(purchaseInformation,
            ((InAppBillingProduct) product).getSku()))
        .toSingle()
        .subscribeOn(Schedulers.io());
  }

  @Override public Single<List<PaymentServiceResponse>> getPayments() {
    return getServerInAppBillingPaymentServices(product.getApiVersion(), product.getPackageName(),
        product.getSku(), product.getType()).toSingle();
  }

  private Observable<List<PaymentServiceResponse>> getServerInAppBillingPaymentServices(
      int apiVersion, String packageName, String sku, String type) {
    return inAppBillingRepository.getSKUDetails(apiVersion, packageName, sku, type)
        .map(response -> response.getPaymentServices());
  }

  private Observable<Purchase> convertToPurchase(
      InAppBillingPurchasesResponse.PurchaseInformation purchaseInformation, String sku) {
    return Observable.zip(Observable.from(purchaseInformation.getPurchaseList()),
        Observable.from(purchaseInformation.getSignatureList()), (purchase, signature) -> {
          if (purchase.getProductId()
              .equals(sku) && purchase.getPurchaseState() == 0) {
            return purchaseFactory.create(purchase, signature);
          }
          return null;
        })
        .filter(purchase -> purchase != null)
        .switchIfEmpty(Observable.error(
            new RepositoryItemNotFoundException("No purchase found for SKU " + sku)))
        .first();
  }
}