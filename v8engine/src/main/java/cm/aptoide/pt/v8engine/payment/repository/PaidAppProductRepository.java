/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.repository;

import android.content.Context;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.model.v3.PaidApp;
import cm.aptoide.pt.v8engine.payment.Payer;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

/**
 * Created by marcelobenites on 29/11/16.
 */

public class PaidAppProductRepository extends ProductRepository {

  private final PurchaseFactory purchaseFactory;
  private final NetworkOperatorManager operatorManager;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final ProductFactory productFactory;

  public PaidAppProductRepository(PurchaseFactory purchaseFactory, PaymentFactory paymentFactory,
      PaymentAuthorizationRepository authorizationRepository,
      PaymentConfirmationRepository confirmationRepository, Payer payer,
      PaymentAuthorizationFactory authorizationFactory, NetworkOperatorManager operatorManager,
      BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory, ProductFactory productFactory) {
    super(paymentFactory, authorizationRepository, confirmationRepository, payer,
        authorizationFactory);
    this.purchaseFactory = purchaseFactory;
    this.operatorManager = operatorManager;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.productFactory = productFactory;
  }

  public Single<Product> getProduct(long appId, boolean sponsored, String storeName) {
    return getPaidApp(false, appId, sponsored, storeName).map(
        paidApp -> productFactory.create(paidApp, sponsored));
  }

  @Override public Single<Purchase> getPurchase(Product product) {
    return getPaidApp(true, ((PaidAppProduct) product).getAppId(),
        ((PaidAppProduct) product).isSponsored(),
        ((PaidAppProduct) product).getStoreName()).flatMap(app -> {
      if (app.getPayment().isPaid()) {
        return Single.just(purchaseFactory.create(app));
      }
      return Single.error(new RepositoryItemNotFoundException(
          "Purchase not found for product " + ((PaidAppProduct) product).getAppId()));
    });
  }

  @Override public Single<List<Payment>> getPayments(Context context, Product product) {
    return getPaidApp(false, ((PaidAppProduct) product).getAppId(),
        ((PaidAppProduct) product).isSponsored(), ((PaidAppProduct) product).getStoreName()).map(
        paidApp -> paidApp.getPayment().getPaymentServices())
        .flatMap(payments -> convertResponseToPayment(context, payments));
  }

  private Single<PaidApp> getPaidApp(boolean refresh, long appId, boolean sponsored,
      String storeName) {
    return GetApkInfoRequest.of(appId, sponsored, storeName, operatorManager, bodyInterceptorV3,
        httpClient, converterFactory).observe(refresh).flatMap(response -> {
      if (response != null && response.isOk() && response.isPaid()) {
        return Observable.just(response);
      } else {
        return Observable.error(new RepositoryItemNotFoundException(
            "No paid app found for app id " + appId + " in store " + storeName));
      }
    }).first().toSingle();
  }
}
