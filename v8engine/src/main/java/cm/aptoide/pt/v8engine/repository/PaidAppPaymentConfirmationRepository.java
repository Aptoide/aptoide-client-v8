/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaymentConfirmationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.sync.SyncAdapterBackgroundSync;
import rx.Completable;
import rx.Observable;

/**
 * Created by marcelobenites on 16/12/16.
 */

public class PaidAppPaymentConfirmationRepository extends PaymentConfirmationRepository {

  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;

  public PaidAppPaymentConfirmationRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor paymentDatabase, SyncAdapterBackgroundSync backgroundSync,
      PaymentConfirmationFactory confirmationFactory, AptoideAccountManager accountManager,
      BodyInterceptor<BaseBody> bodyInterceptorV3) {
    super(operatorManager, paymentDatabase, backgroundSync, confirmationFactory);
    this.accountManager = accountManager;
    this.bodyInterceptorV3 = bodyInterceptorV3;
  }

  @Override public Completable createPaymentConfirmation(int paymentId, Product product) {
    return CreatePaymentConfirmationRequest.ofPaidApp(product.getId(), paymentId, operatorManager,
        ((PaidAppProduct) product).getStoreName(), accountManager.getAccessToken(),
        bodyInterceptorV3).observe().flatMap(response -> {
      if (response != null && response.isOk()) {
        return Observable.just(null);
      }
      return Observable.error(new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
    }).toCompletable().andThen(syncPaymentConfirmation(product));
  }

  @Override
  public Completable createPaymentConfirmation(int paymentId, String paymentConfirmationId,
      Product product) {
    return createPaymentConfirmation(product, paymentId, paymentConfirmationId);
  }
}
