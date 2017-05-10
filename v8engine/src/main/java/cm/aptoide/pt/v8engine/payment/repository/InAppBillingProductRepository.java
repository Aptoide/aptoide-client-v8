/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.repository;

import android.content.Context;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.payment.Payer;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.products.InAppBillingProduct;
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
        ((InAppBillingProduct) product).getApiVersion(),
        ((InAppBillingProduct) product).getPackageName(), ((InAppBillingProduct) product).getType())
        .flatMap(purchaseInformation -> convertToPurchase(purchaseInformation,
            ((InAppBillingProduct) product)))
        .toSingle()
        .subscribeOn(Schedulers.io());
  }

  @Override public Single<List<Payment>> getPayments(Context context, Product product) {
    return getServerInAppBillingPaymentServices(((InAppBillingProduct) product).getApiVersion(),
        ((InAppBillingProduct) product).getPackageName(), ((InAppBillingProduct) product).getSku(),
        ((InAppBillingProduct) product).getType()).flatMap(
        payments -> convertResponseToPayment(context, payments));
  }

  private Single<List<PaymentServiceResponse>> getServerInAppBillingPaymentServices(int apiVersion,
      String packageName, String sku, String type) {
    return inAppBillingRepository.getSKUDetails(apiVersion, packageName, sku, type)
        .map(response -> response.getPaymentServices());
  }

  private Observable<Purchase> convertToPurchase(
      InAppBillingPurchasesResponse.PurchaseInformation purchaseInformation, InAppBillingProduct product) {
    return Observable.zip(Observable.from(purchaseInformation.getPurchaseList()),
        Observable.from(purchaseInformation.getSignatureList()), (purchase, signature) -> {
          if (purchase.getProductId().equals(product.getSku()) && purchase.getPurchaseState() == 0) {
            return purchaseFactory.create(purchase, signature);
          }
          return null;
        })
        .filter(purchase -> purchase != null)
        .switchIfEmpty(Observable.error(
            new RepositoryItemNotFoundException("No purchase found for SKU " + product)))
        .first();
  }
}