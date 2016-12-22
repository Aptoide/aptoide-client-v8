/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePurchaseAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.model.v3.PurchaseAuthorizationResponse;
import cm.aptoide.pt.v8engine.payment.PaymentAuthorization;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.sync.SyncAdapterBackgroundSync;
import rx.Completable;
import rx.Observable;

public class PaymentAuthorizationRepository implements Repository {
  private final PaymentAuthorizationAccessor authotizationAccessor;
  private final SyncAdapterBackgroundSync backgroundSync;
  private final PaymentAuthorizationConverter auhorizationConverter;

  public PaymentAuthorizationRepository(PaymentAuthorizationAccessor authorizationAccessor,
      SyncAdapterBackgroundSync backgroundSync,
      PaymentAuthorizationConverter authorizationConverter) {
    this.authotizationAccessor = authorizationAccessor;
    this.backgroundSync = backgroundSync;
    this.auhorizationConverter = authorizationConverter;
  }

  public Completable createPaymentAuthorization(int paymentId) {
    return CreatePurchaseAuthorizationRequest.of(AptoideAccountManager.getAccessToken(), paymentId)
        .observe()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            syncPaymentAuthorization(paymentId, response);
            return Observable.just(null);
          }
          return Observable.<Void>error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        })
        .toCompletable();
  }

  public Observable<PaymentAuthorization> getPaymentAuthorization(int paymentId) {
    return authotizationAccessor.getPaymentAuthorizations(paymentId)
        .flatMap(paymentAuthorizations -> Observable.from(paymentAuthorizations)
            .map(paymentAuthorization -> auhorizationConverter.convertToPaymentAuthorization(
                paymentAuthorization))
            .defaultIfEmpty(
                new PaymentAuthorization(paymentId, "", "", PaymentAuthorization.Status.ERROR)))
        .doOnSubscribe(() -> syncPaymentAuthorization(paymentId));
  }

  private void syncPaymentAuthorization(int paymentId) {
    authotizationAccessor.save(
        new cm.aptoide.pt.database.realm.PaymentAuthorization(paymentId, null, null,
            PaymentAuthorization.Status.SYNCING.name()));
    backgroundSync.syncAuthorization(paymentId);
  }

  private void syncPaymentAuthorization(int paymentId, PurchaseAuthorizationResponse response) {
    authotizationAccessor.save(
        auhorizationConverter.convertToDatabasePaymentAuthorization(paymentId, response));
    backgroundSync.syncAuthorization(paymentId);
  }
}