/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.billing.exception.MerchantNotFoundException;
import cm.aptoide.pt.billing.exception.ProductNotFoundException;
import cm.aptoide.pt.billing.exception.PurchaseNotFoundException;
import cm.aptoide.pt.billing.product.ProductFactory;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.billing.DeletePurchaseRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetMerchantRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetProductsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetPurchasesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetServicesRequest;
import cm.aptoide.pt.install.PackageRepository;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Single;

public class V7BillingService implements BillingService {

  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final PurchaseMapper purchaseMapper;
  private final ProductFactory productFactory;
  private final PackageRepository packageRepository;
  private final PaymentServiceMapper serviceMapper;
  private final BodyInterceptor<BaseBody> bodyInterceptorV7;

  public V7BillingService(BodyInterceptor<BaseBody> bodyInterceptorV7, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, PurchaseMapper purchaseMapper,
      ProductFactory productFactory, PackageRepository packageRepository,
      PaymentServiceMapper serviceMapper) {
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.purchaseMapper = purchaseMapper;
    this.productFactory = productFactory;
    this.packageRepository = packageRepository;
    this.serviceMapper = serviceMapper;
    this.bodyInterceptorV7 = bodyInterceptorV7;
  }

  @Override public Single<List<PaymentService>> getPaymentServices() {
    return GetServicesRequest.of(sharedPreferences, httpClient, converterFactory, bodyInterceptorV7,
        tokenInvalidator)
        .observe(false)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(serviceMapper.map(response.getList()));
          } else {
            return Single.error(new IllegalArgumentException(V7.getErrorMessage(response)));
          }
        });
  }

  @Override public Single<Merchant> getMerchant(String merchantName) {
    return GetMerchantRequest.of(merchantName, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(false)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(new Merchant(response.getData()
                .getId(), response.getData()
                .getName()));
          } else {
            return Single.error(new MerchantNotFoundException(V7.getErrorMessage(response)));
          }
        });
  }

  @Override public Completable deletePurchase(long purchaseId) {
    return DeletePurchaseRequest.of(purchaseId, httpClient, converterFactory, bodyInterceptorV7,
        tokenInvalidator, sharedPreferences)
        .observe(true)
        .first()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response != null && response.isOk()) {
            return Completable.complete();
          }
          return Completable.error(new PurchaseNotFoundException(V7.getErrorMessage(response)));
        });
  }

  @Override public Single<List<Purchase>> getPurchases(String merchantName) {
    return GetPurchasesRequest.of(merchantName, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(purchaseMapper.map(response.getList()));
          }
          // If user not logged in return a empty purchase list.
          return Single.<List<Purchase>>just(Collections.emptyList());
        });
  }

  @Override public Single<Purchase> getProductPurchase(long productId) {
    return GetPurchasesRequest.ofProduct(productId, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(purchaseMapper.map(response.getData()));
          }
          return Single.error(new PurchaseNotFoundException());
        });
  }

  @Override public Single<Purchase> getPurchase(long purchaseId) {
    return GetPurchasesRequest.of(purchaseId, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(purchaseMapper.map(response.getData()));
          }
          return Single.error(new PurchaseNotFoundException());
        });
  }

  @Override public Single<List<Product>> getProducts(String merchantName, List<String> skus) {
    return GetProductsRequest.of(merchantName, skus, bodyInterceptorV7, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe(false)
        .first()
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return mapToProducts(merchantName, response.getList());
          } else {
            return Single.<List<Product>>error(
                new IllegalArgumentException(V7.getErrorMessage(response)));
          }
        });
  }

  @Override public Single<Product> getProduct(String sku, String merchantName) {
    return GetProductsRequest.of(merchantName, sku, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(false)
        .first()
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return mapToProduct(merchantName, response.getData());
          } else {
            return Single.error(new ProductNotFoundException("No product found for sku: " + sku));
          }
        });
  }

  private Single<List<Product>> mapToProducts(String merchantName,
      List<GetProductsRequest.ResponseBody.Product> responseList) {
    return Single.zip(packageRepository.getPackageVersionCode(merchantName),
        packageRepository.getPackageLabel(merchantName),
        (packageVersionCode, applicationName) -> productFactory.create(merchantName, responseList,
            packageVersionCode, applicationName));
  }

  private Single<Product> mapToProduct(String merchantName,
      GetProductsRequest.ResponseBody.Product response) {
    return Single.zip(packageRepository.getPackageVersionCode(merchantName),
        packageRepository.getPackageLabel(merchantName),
        (packageVersionCode, applicationName) -> productFactory.create(merchantName,
            packageVersionCode, applicationName, response));
  }
}
