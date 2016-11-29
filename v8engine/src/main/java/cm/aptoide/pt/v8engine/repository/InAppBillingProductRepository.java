/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.content.Context;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.PaymentService;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.PurchaseFactory;
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
import cm.aptoide.pt.v8engine.payment.product.InAppBillingProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class InAppBillingProductRepository implements ProductRepository {

  final InAppBillingRepository inAppBillingRepository;
  final PurchaseFactory purchaseFactory;
  final PaymentFactory paymentFactory;

  public InAppBillingProductRepository(InAppBillingRepository inAppBillingRepository,
      PurchaseFactory purchaseFactory, PaymentFactory paymentFactory) {
    this.inAppBillingRepository = inAppBillingRepository;
    this.purchaseFactory = purchaseFactory;
    this.paymentFactory = paymentFactory;
  }

  @Override public Observable<Purchase> getPurchase(AptoideProduct product) {
    return inAppBillingRepository.getInAppPurchaseInformation(
        ((InAppBillingProduct) product).getApiVersion(),
        ((InAppBillingProduct) product).getPackageName(), ((InAppBillingProduct) product).getType())
        .flatMap(purchaseInformation -> convertToPurchase(purchaseInformation,
            ((InAppBillingProduct) product).getSku()))
        .subscribeOn(Schedulers.io());
  }

  @Override public Observable<List<Payment>> getPayments(Context context, AptoideProduct product) {
    return getServerInAppBillingPaymentServices(((InAppBillingProduct) product).getApiVersion(),
        ((InAppBillingProduct) product).getPackageName(), ((InAppBillingProduct) product).getSku(),
        ((InAppBillingProduct) product).getType()).flatMapIterable(
        paymentServices -> paymentServices)
        .map(paymentService -> paymentFactory.create(context, paymentService, product))
        .toList()
        .subscribeOn(Schedulers.io());
  }

  private Observable<Purchase> convertToPurchase(
      InAppBillingPurchasesResponse.PurchaseInformation purchaseInformation, String sku) {
    return Observable.zip(Observable.from(purchaseInformation.getPurchaseList()),
        Observable.from(purchaseInformation.getSignatureList()), (purchase, signature) -> {
          if (purchase.getProductId().equals(sku) && purchase.getPurchaseState() == 0) {
            return purchaseFactory.create(purchase, signature);
          }
          return null;
        })
        .filter(purchase -> purchase != null)
        .switchIfEmpty(Observable.error(
            new RepositoryItemNotFoundException("No purchase found for SKU " + sku)))
        .first();
  }

  private Observable<List<PaymentService>> getServerInAppBillingPaymentServices(int apiVersion,
      String packageName, String sku, String type) {
    return inAppBillingRepository.getSKUDetails(apiVersion, packageName, sku, type)
        .map(response -> response.getPaymentServices());
  }
}