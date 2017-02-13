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
  private final AptoideAccountManager accountManager;

  public PaidAppPaymentConfirmationRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor paymentDatabase, SyncAdapterBackgroundSync backgroundSync,
      PaymentConfirmationFactory confirmationFactory, PaidAppProduct product,
      AptoideAccountManager accountManager) {
    super(operatorManager, paymentDatabase, backgroundSync, confirmationFactory);
    this.product = product;
    this.accountManager = accountManager;
  }

  @Override public Completable createPaymentConfirmation(int paymentId) {
    return CreatePaymentConfirmationRequest.ofPaidApp(product.getId(), paymentId, operatorManager,
        product.getStoreName(), accountManager.getAccessToken())
        .observe()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Observable.just(null);
          }
          return Observable.error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        })
        .toCompletable()
        .andThen(syncPaymentConfirmations(product, paymentId));
  }

  @Override
  public Completable createPaymentConfirmation(int paymentId, String paymentConfirmationId) {
    return createPaymentConfirmation(product, paymentId, paymentConfirmationId);
  }
}
