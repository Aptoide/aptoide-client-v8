/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.PaymentMethodMapper;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.PurchaseFactory;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

public class PaidAppProductRepository extends ProductRepository {

  private final PurchaseFactory purchaseFactory;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final ProductFactory productFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;

  public PaidAppProductRepository(PurchaseFactory purchaseFactory,
      PaymentMethodMapper paymentMethodMapper, BodyInterceptor<BaseBody> bodyInterceptorV3,
      OkHttpClient httpClient, Converter.Factory converterFactory, ProductFactory productFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, Resources resources) {
    super(paymentMethodMapper);
    this.purchaseFactory = purchaseFactory;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.productFactory = productFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
  }

  public Single<Product> getProduct(long appId, boolean sponsored, String storeName) {
    return getServerPaidApp(false, appId, sponsored, storeName).map(
        paidApp -> productFactory.create(paidApp, sponsored));
  }

  @Override public Single<Purchase> getPurchase(Product product) {
    return getServerPaidApp(true, ((PaidAppProduct) product).getAppId(),
        ((PaidAppProduct) product).isSponsored(),
        ((PaidAppProduct) product).getStoreName()).flatMap(app -> {
      if (app.getPayment()
          .isPaid()) {
        return Single.just(purchaseFactory.create(app));
      }
      return Single.error(new RepositoryIllegalArgumentException(
          "Purchase not found for product " + ((PaidAppProduct) product).getAppId()));
    });
  }

  @Override public Single<List<PaymentMethod>> getPaymentMethods(Product product) {
    return getServerPaidApp(false, ((PaidAppProduct) product).getAppId(),
        ((PaidAppProduct) product).isSponsored(), ((PaidAppProduct) product).getStoreName()).map(
        paidApp -> paidApp.getPayment()
            .getPaymentServices())
        .flatMap(payments -> convertResponsesToPaymentMethods(payments));
  }

  private Single<PaidApp> getServerPaidApp(boolean bypassCache, long appId, boolean sponsored,
      String storeName) {
    return GetApkInfoRequest.of(appId, sponsored, storeName, bodyInterceptorV3, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences, resources)
        .observe(bypassCache)
        .flatMap(response -> {
          if (response != null && response.isOk() && response.isPaid()) {
            return Observable.just(response);
          } else {
            return Observable.error(
                new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
          }
        })
        .first()
        .toSingle();
  }
}
