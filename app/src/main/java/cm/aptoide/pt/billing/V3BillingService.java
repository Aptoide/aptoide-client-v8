/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.billing;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.pt.billing.product.ProductFactory;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class V3BillingService implements BillingService {

  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final PurchaseMapper purchaseMapper;
  private final ProductFactory productFactory;
  private final Resources resources;
  private final PaymentService paymentService;

  public V3BillingService(BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, PurchaseMapper purchaseMapper,
      ProductFactory productFactory, Resources resources, PaymentService paymentService) {
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.purchaseMapper = purchaseMapper;
    this.productFactory = productFactory;
    this.resources = resources;
    this.paymentService = paymentService;
  }

  @Override public Single<List<PaymentService>> getPaymentServices() {
    return Single.just(Collections.singletonList(paymentService));
  }

  @Override public Single<Merchant> getMerchant(String merchantName) {
    return Single.just(new Merchant(-1, merchantName));
  }

  @Override public Single<Purchase> getProductPurchase(long productId) {
    return getServerPaidApp(true, productId).map(app -> purchaseMapper.map(app));
  }

  @Override public Single<Product> getProduct(String sku, String merchantName) {
    return getServerPaidApp(false, Long.valueOf(sku)).map(
        paidApp -> productFactory.create(paidApp));
  }

  @Override public Completable deletePurchase(long purchaseId) {
    return Completable.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<Purchase> getPurchase(long purchaseId) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<List<Purchase>> getPurchases(String merchantName) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<List<Product>> getProducts(String sellerId, List<String> productIds) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  private Single<PaidApp> getServerPaidApp(boolean bypassCache, long appId) {
    return GetApkInfoRequest.of(appId, bodyInterceptorV3, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, resources)
        .observe(bypassCache)
        .flatMap(response -> {
          if (response != null && response.isOk() && response.isPaid()) {
            return Observable.just(response);
          } else {
            return Observable.error(new IllegalArgumentException(V3.getErrorMessage(response)));
          }
        })
        .first()
        .toSingle();
  }
}
