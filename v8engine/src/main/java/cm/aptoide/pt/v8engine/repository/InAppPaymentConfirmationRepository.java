/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaymentConfirmationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.v8engine.payment.products.InAppBillingProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.sync.SyncAdapterBackgroundSync;
import rx.Completable;
import rx.Observable;

/**
 * Created by marcelobenites on 8/18/16.
 */
public class InAppPaymentConfirmationRepository extends PaymentConfirmationRepository {

  private final InAppBillingProduct product;

  public InAppPaymentConfirmationRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor paymentDatabase, SyncAdapterBackgroundSync backgroundSync,
      PaymentConfirmationFactory confirmationFactory, InAppBillingProduct product) {
    super(operatorManager, paymentDatabase, backgroundSync, confirmationFactory);
    this.product = product;
  }

  @Override public Completable createPaymentConfirmation(int paymentId) {
    return CreatePaymentConfirmationRequest.ofInApp(product.getId(), paymentId, operatorManager,
        product.getDeveloperPayload(), AptoideAccountManager.getAccessToken())
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