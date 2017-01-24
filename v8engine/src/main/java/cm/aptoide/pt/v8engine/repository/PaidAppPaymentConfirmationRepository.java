/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaymentConfirmationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.sync.SyncAdapterBackgroundSync;
import rx.Completable;
import rx.Observable;

/**
 * Created by marcelobenites on 16/12/16.
 */

public class PaidAppPaymentConfirmationRepository extends PaymentConfirmationRepository {

  private final PaidAppProduct product;

  public PaidAppPaymentConfirmationRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor paymentDatabase, SyncAdapterBackgroundSync backgroundSync,
      PaymentConfirmationFactory confirmationFactory, PaidAppProduct product) {
    super(operatorManager, paymentDatabase, backgroundSync, confirmationFactory);
    this.product = product;
  }

  @Override public Completable createPaymentConfirmation(int paymentId) {
    return CreatePaymentConfirmationRequest.ofPaidApp(product.getId(), paymentId, operatorManager,
        product.getStoreName(), AptoideAccountManager.getAccessToken())
        .observe()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            syncPaymentConfirmation(product);
            return Observable.just(null);
          }
          return Observable.error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        }).toCompletable();
  }

  @Override public Completable createPaymentConfirmation(int paymentId,
      String paymentConfirmationId) {
    return createPaymentConfirmation(paymentId, paymentConfirmationId, product);
  }
}
