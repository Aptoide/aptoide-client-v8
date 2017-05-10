/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.repository;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.model.v3.PaidApp;
import cm.aptoide.pt.v8engine.payment.Payer;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.ProductRepository;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.PurchaseFactory;
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
  private final PaymentFactory paymentFactory;
  private final PaidAppProduct product;
  private final NetworkOperatorManager operatorManager;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;

  public PaidAppProductRepository(PurchaseFactory purchaseFactory, PaymentFactory paymentFactory,
      PaidAppProduct product, PaymentAuthorizationRepository authorizationRepository,
      PaymentConfirmationRepository confirmationRepository, Payer payer,
      PaymentAuthorizationFactory authorizationFactory, NetworkOperatorManager operatorManager,
      BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    super(paymentFactory, authorizationRepository, confirmationRepository, payer,
        authorizationFactory);
    this.purchaseFactory = purchaseFactory;
    this.paymentFactory = paymentFactory;
    this.product = product;
    this.operatorManager = operatorManager;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
  }

  @Override public Single<Purchase> getPurchase(Product product) {
    final PaidAppProduct paidAppProduct = (PaidAppProduct) product;
    return getPaidApp(paidAppProduct.getAppId(), false, paidAppProduct.getStoreName(),
        true).toSingle().flatMap(app -> {
      if (app.getPayment().isPaid()) {
        return Single.just(purchaseFactory.create(app));
      }
      return Single.error(new RepositoryItemNotFoundException(
          "Purchase not found for product " + paidAppProduct.getId()));
    });
  }

  @Override public Single<List<Payment>> getPayments() {
    return getPaidApp(product.getAppId(), product.isSponsored(), product.getStoreName(), false).map(
        paidApp -> paidApp.getPayment().getPaymentServices())
        .toSingle()
        .flatMap(payments -> convertResponseToPayment(payments));
  }

  private Observable<PaidApp> getPaidApp(long appId, boolean sponsored, String storeName,
      boolean refresh) {
    return GetApkInfoRequest.of(appId, operatorManager, sponsored, storeName, bodyInterceptorV3,
        httpClient, converterFactory).observe(refresh).flatMap(response -> {
      if (response != null && response.isOk() && response.isPaid()) {
        return Observable.just(response);
      } else {
        return Observable.error(new RepositoryItemNotFoundException(
            "No paid app found for app id " + appId + " in store " + storeName));
      }
    });
  }
}
