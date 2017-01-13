/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/11/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.PurchaseFactory;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import rx.Single;

/**
 * Created by marcelobenites on 29/11/16.
 */

public class PaidAppProductRepository implements ProductRepository {

  private final AppRepository appRepository;
  private final PurchaseFactory purchaseFactory;
  private final PaymentFactory paymentFactory;

  public PaidAppProductRepository(AppRepository appRepository, PurchaseFactory purchaseFactory,
      PaymentFactory paymentFactory) {
    this.appRepository = appRepository;
    this.purchaseFactory = purchaseFactory;
    this.paymentFactory = paymentFactory;
  }

  @Override public Single<Purchase> getPurchase(AptoideProduct product) {
    final PaidAppProduct paidAppProduct = (PaidAppProduct) product;
    return appRepository.getPaidApp(paidAppProduct.getAppId(), false,
        paidAppProduct.getStoreName(), true).toSingle().flatMap(app -> {
      if (app.getPayment().isPaid()) {
        return Single.just(purchaseFactory.create(app));
      }
      return Single.error(new RepositoryItemNotFoundException(
          "Purchase not found for product " + paidAppProduct.getId()));
    });
  }

  @Override public Single<List<Payment>> getPayments(AptoideProduct product) {
    return getServerPaidAppPaymentServices(((PaidAppProduct) product).getAppId(), false,
        ((PaidAppProduct) product).getStoreName(), true).toObservable()
        .flatMapIterable(paymentServices -> paymentServices)
        .map(paymentService -> paymentFactory.create(paymentService, product))
        .toList().toSingle();
  }

  private Single<List<PaymentServiceResponse>> getServerPaidAppPaymentServices(long appId,
      boolean sponsored, String storeName, boolean refresh) {
    return appRepository.getPaidApp(appId, sponsored, storeName, refresh)
        .map(paidApp -> paidApp.getPayment().getPaymentServices()).toSingle();
  }
}
