/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaymentAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.v8engine.payment.Authorization;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.sync.SyncAdapterBackgroundSync;
import java.util.List;
import rx.Completable;
import rx.Observable;

public class PaymentAuthorizationRepository implements Repository {
  private final PaymentAuthorizationAccessor authotizationAccessor;
  private final SyncAdapterBackgroundSync backgroundSync;
  private final PaymentAuthorizationFactory auhorizationConverter;

  public PaymentAuthorizationRepository(PaymentAuthorizationAccessor authorizationAccessor,
      SyncAdapterBackgroundSync backgroundSync,
      PaymentAuthorizationFactory authorizationConverter) {
    this.authotizationAccessor = authorizationAccessor;
    this.backgroundSync = backgroundSync;
    this.auhorizationConverter = authorizationConverter;
  }

  public Completable createPaymentAuthorization(int paymentId) {
    return CreatePaymentAuthorizationRequest.of(AptoideAccountManager.getAccessToken(), paymentId)
        .observe()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            backgroundSync.syncAuthorization(paymentId);
            return Observable.just(null);
          }
          return Observable.<Void>error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        })
        .toCompletable();
  }

  public Observable<Authorization> getPaymentAuthorization(int paymentId) {
    return authotizationAccessor.getPaymentAuthorizations(paymentId)
        .flatMap(paymentAuthorizations -> Observable.from(paymentAuthorizations)
            .map(paymentAuthorization -> auhorizationConverter.convertToPaymentAuthorization(
                paymentAuthorization)))
        .doOnSubscribe(() -> backgroundSync.syncAuthorization(paymentId));
  }

  public Observable<List<Authorization>> getPaymentAuthorizations(List<Integer> paymentIds) {
    return syncPayments(paymentIds).andThen(authotizationAccessor.getPaymentAuthorizations()
        .flatMap(paymentAuthorizations -> Observable.from(paymentAuthorizations)
            .map(paymentAuthorization -> auhorizationConverter.convertToPaymentAuthorization(
                paymentAuthorization))
            .toList()));
  }

  private Completable syncPayments(List<Integer> paymentIds) {
    return Observable.from(paymentIds)
        .map(paymentId -> String.valueOf(paymentId))
        .toList()
        .doOnNext(stringIds -> backgroundSync.syncAuthorizations(stringIds))
        .toCompletable();
  }
}