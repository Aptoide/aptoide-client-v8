/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import android.content.Context;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Payment;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class InAppBillingProductRepository extends ProductRepository {

  private final InAppBillingRepository inAppBillingRepository;
  private final PurchaseFactory purchaseFactory;
  private final PaymentFactory paymentFactory;
  private final ProductFactory productFactory;

  public InAppBillingProductRepository(InAppBillingRepository inAppBillingRepository,
      PurchaseFactory purchaseFactory, PaymentFactory paymentFactory,
      PaymentAuthorizationRepository authorizationRepository,
      PaymentConfirmationRepository confirmationRepository, Payer payer,
      PaymentAuthorizationFactory authorizationFactory, ProductFactory productFactory) {
    super(paymentFactory, authorizationRepository, confirmationRepository, payer,
        authorizationFactory);
    this.inAppBillingRepository = inAppBillingRepository;
    this.purchaseFactory = purchaseFactory;
    this.paymentFactory = paymentFactory;
    this.productFactory = productFactory;
  }

  public Single<Product> getProduct(int apiVersion, String packageName, String sku, String type,
      String developerPayload) {
    return inAppBillingRepository.getSKUDetails(apiVersion, packageName, sku, type)
        .map(
            response -> productFactory.create(apiVersion, developerPayload, packageName, response));
  }

  @Override public Single<Purchase> getPurchase(Product product) {
    return inAppBillingRepository.getInAppPurchaseInformation(
        ((InAppProduct) product).getApiVersion(), ((InAppProduct) product).getPackageName(),
        ((InAppProduct) product).getType())
        .flatMap(
            purchaseInformation -> convertToPurchase(purchaseInformation, ((InAppProduct) product)))
        .toSingle()
        .subscribeOn(Schedulers.io());
  }

  @Override public Single<List<Payment>> getPayments(Context context, Product product) {
    return getServerInAppBillingPaymentServices(((InAppProduct) product).getApiVersion(),
        ((InAppProduct) product).getPackageName(), ((InAppProduct) product).getSku(),
        ((InAppProduct) product).getType()).flatMap(
        payments -> convertResponseToPayment(context, payments));
  }

  public Single<Purchase> getPurchase(int apiVersion, String packageName, String purchaseToken,
      String type) {
    return inAppBillingRepository.getInAppPurchaseInformation(apiVersion, packageName, type)
        .flatMap(purchaseInformation -> convertToPurchase(purchaseInformation, purchaseToken,
            apiVersion))
        .toSingle()
        .subscribeOn(Schedulers.io());
  }

  private Single<List<PaymentServiceResponse>> getServerInAppBillingPaymentServices(int apiVersion,
      String packageName, String sku, String type) {
    return inAppBillingRepository.getSKUDetails(apiVersion, packageName, sku, type)
        .map(response -> response.getPaymentServices());
  }

  private Observable<Purchase> convertToPurchase(
      InAppBillingPurchasesResponse.PurchaseInformation purchaseInformation, InAppProduct product) {
    return Observable.zip(Observable.from(purchaseInformation.getPurchaseList()),
        Observable.from(purchaseInformation.getSignatureList()), (purchase, signature) -> {
          if (purchase.getProductId().equals(product.getSku())
              && purchase.getPurchaseState() == 0) {
            return purchaseFactory.create(purchase, signature, product.getApiVersion());
          }
          return null;
        })
        .filter(purchase -> purchase != null)
        .switchIfEmpty(Observable.error(
            new RepositoryItemNotFoundException("No purchase found for SKU " + product)))
        .first();
  }

  private Observable<Purchase> convertToPurchase(
      InAppBillingPurchasesResponse.PurchaseInformation purchaseInformation, String purchaseToken,
      int apiVersion) {
    return Observable.zip(Observable.from(purchaseInformation.getPurchaseList()),
        Observable.from(purchaseInformation.getSignatureList()), (purchase, signature) -> {
          if (purchase.getPurchaseToken().equals(purchaseToken)) {
            return purchaseFactory.create(purchase, signature, apiVersion);
          }
          return null;
        })
        .filter(purchase -> purchase != null)
        .switchIfEmpty(Observable.error(
            new RepositoryItemNotFoundException("No purchase found for token" + purchaseToken)))
        .first();
  }
}