/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.billing.networking;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.BillingService;
import cm.aptoide.pt.billing.Merchant;
import cm.aptoide.pt.billing.payment.PaymentService;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Single;

public class BillingServiceV3 implements BillingService {

  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final PurchaseMapperV3 purchaseMapper;
  private final ProductMapperV3 productMapper;
  private final Resources resources;
  private final PaymentService paymentService;
  private final BillingIdManager billingIdManager;
  private final int currentAPILevel;
  private final int serviceMinimumAPILevel;

  public BillingServiceV3(BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, PurchaseMapperV3 purchaseMapper,
      ProductMapperV3 productMapper, Resources resources, PaymentService paymentService,
      BillingIdManager billingIdManager, int currentAPILevel, int serviceMinimumAPILevel) {
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.purchaseMapper = purchaseMapper;
    this.productMapper = productMapper;
    this.resources = resources;
    this.paymentService = paymentService;
    this.billingIdManager = billingIdManager;
    this.currentAPILevel = currentAPILevel;
    this.serviceMinimumAPILevel = serviceMinimumAPILevel;
  }

  @Override public Single<List<PaymentService>> getPaymentServices() {
    if (currentAPILevel >= serviceMinimumAPILevel) {
      return Single.just(Collections.singletonList(paymentService));
    }
    return Single.just(Collections.emptyList());
  }

  @Override public Single<Merchant> getMerchant(String merchantName) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<Purchase> getPurchase(String productId) {
    return getServerPaidApp(true, billingIdManager.resolveProductId(productId)).map(
        app -> purchaseMapper.map(app, productId));
  }

  @Override public Single<Product> getProduct(String sku, String merchantName) {
    return getServerPaidApp(false, billingIdManager.resolveProductId(sku)).map(
        paidApp -> productMapper.map(paidApp));
  }

  @Override public Completable deletePurchase(String purchaseId) {
    return Completable.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<List<Purchase>> getPurchases(String merchantName) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<List<Product>> getProducts(String merchantName, List<String> productIds) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  private Single<PaidApp> getServerPaidApp(boolean bypassCache, long appId) {
    return GetApkInfoRequest.of(appId, bodyInterceptorV3, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, resources)
        .observe(bypassCache)
        .first()
        .toSingle();
  }
}
