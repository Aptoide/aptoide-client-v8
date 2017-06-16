/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingPurchasesRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingSkuDetailsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Payment;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class InAppBillingProductRepository extends ProductRepository {

  private final PurchaseFactory purchaseFactory;
  private final ProductFactory productFactory;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final NetworkOperatorManager operatorManager;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public InAppBillingProductRepository(PurchaseFactory purchaseFactory,
      PaymentFactory paymentFactory, AuthorizationRepository authorizationRepository,
      PaymentConfirmationRepository confirmationRepository, Payer payer,
      AuthorizationFactory authorizationFactory, ProductFactory productFactory,
      BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory, NetworkOperatorManager operatorManager,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(paymentFactory);
    this.purchaseFactory = purchaseFactory;
    this.productFactory = productFactory;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.operatorManager = operatorManager;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public Single<Purchase> getPurchase(Product product) {
    return getServerInAppPurchase(((InAppProduct) product).getApiVersion(),
        ((InAppProduct) product).getPackageName(), ((InAppProduct) product).getType(),
        true).flatMap(
        purchaseInformation -> convertToPurchase(purchaseInformation, ((InAppProduct) product)))
        .toSingle()
        .subscribeOn(Schedulers.io());
  }

  @Override public Single<List<Payment>> getPayments(Product product) {
    return getServerInAppBillingPaymentServices(((InAppProduct) product).getApiVersion(),
        ((InAppProduct) product).getPackageName(), ((InAppProduct) product).getSku(),
        ((InAppProduct) product).getType(), false).flatMap(
        payments -> convertResponseToPayment(payments));
  }

  public Single<Product> getProduct(int apiVersion, String packageName, String sku, String type,
      String developerPayload) {
    return getServerSKUs(apiVersion, packageName, Collections.singletonList(sku), type, false).map(
        response -> productFactory.create(apiVersion, developerPayload, packageName, response)
            .get(0));
  }

  public Single<List<Product>> getProducts(int apiVersion, String packageName, List<String> skus,
      String type) {
    return getServerSKUs(apiVersion, packageName, skus, type, false).map(
        response -> productFactory.create(apiVersion, null, packageName, response));
  }

  public Single<Purchase> getPurchase(int apiVersion, String packageName, String purchaseToken,
      String type) {
    return getServerInAppPurchase(apiVersion, packageName, type, false).flatMap(
        purchaseInformation -> convertToPurchase(purchaseInformation, purchaseToken, apiVersion))
        .toSingle()
        .subscribeOn(Schedulers.io());
  }

  public Single<List<Purchase>> getPurchases(int apiVersion, String packageName, String type) {
    return getServerInAppPurchase(apiVersion, packageName, type, true).first()
        .toSingle()
        .flatMap(purchaseInformation -> convertToPurchases(purchaseInformation, apiVersion));
  }

  private Single<List<PaymentServiceResponse>> getServerInAppBillingPaymentServices(int apiVersion,
      String packageName, String sku, String type, boolean bypassCache) {
    return getServerSKUs(apiVersion, packageName, Collections.singletonList(sku), type,
        bypassCache).map(response -> response.getPaymentServices());
  }

  private Observable<Purchase> convertToPurchase(
      InAppBillingPurchasesResponse.PurchaseInformation purchaseInformation, InAppProduct product) {
    return Observable.zip(Observable.from(purchaseInformation.getPurchaseList()),
        Observable.from(purchaseInformation.getSignatureList()), (purchase, signature) -> {
          if (purchase.getProductId()
              .equals(product.getSku()) && purchase.getPurchaseState() == 0) {
            return purchaseFactory.create(purchase, signature, product.getApiVersion(),
                purchase.getProductId());
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
          if (purchase.getPurchaseToken()
              .equals(purchaseToken) && purchase.getPurchaseState() == 0) {
            return purchaseFactory.create(purchase, signature, apiVersion, purchase.getProductId());
          }
          return null;
        })
        .filter(purchase -> purchase != null)
        .switchIfEmpty(Observable.error(
            new RepositoryItemNotFoundException("No purchase found for token" + purchaseToken)))
        .first();
  }

  private Single<List<Purchase>> convertToPurchases(
      InAppBillingPurchasesResponse.PurchaseInformation purchaseInformation, int apiVersion) {
    return Observable.zip(Observable.from(purchaseInformation.getPurchaseList()),
        Observable.from(purchaseInformation.getSignatureList()), (purchase, signature) -> {
          return purchaseFactory.create(purchase, signature, apiVersion, purchase.getProductId());
        })
        .toList()
        .toSingle();
  }

  private Observable<InAppBillingPurchasesResponse.PurchaseInformation> getServerInAppPurchase(
      int apiVersion, String packageName, String type, boolean bypassCache) {
    return InAppBillingPurchasesRequest.of(apiVersion, packageName, type, bodyInterceptorV3,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe(bypassCache)
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Observable.just(response.getPurchaseInformation());
          }
          return Observable.error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        });
  }

  private Single<InAppBillingSkuDetailsResponse> getServerSKUs(int apiVersion, String packageName,
      List<String> skuList, String type, boolean bypassCache) {
    return InAppBillingSkuDetailsRequest.of(apiVersion, packageName, skuList, operatorManager, type,
        bodyInterceptorV3, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe(bypassCache)
        .first()
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(response);
          } else {
            final List<InAppBillingSkuDetailsResponse.PurchaseDataObject> detailList =
                response.getPublisherResponse()
                    .getDetailList();
            if (detailList.isEmpty()) {
              return Single.error(
                  new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
            }
            return Single.error(
                new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
          }
        });
  }
}