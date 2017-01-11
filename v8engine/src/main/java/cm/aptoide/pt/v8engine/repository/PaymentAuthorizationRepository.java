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
import java.util.Collections;
import java.util.List;
import rx.Completable;
import rx.Observable;

public class PaymentAuthorizationRepository implements Repository {
  private final PaymentAuthorizationAccessor authotizationAccessor;
  private final SyncAdapterBackgroundSync backgroundSync;
  private final PaymentAuthorizationFactory authorizationFactory;

  public PaymentAuthorizationRepository(PaymentAuthorizationAccessor authorizationAccessor,
      SyncAdapterBackgroundSync backgroundSync, PaymentAuthorizationFactory authorizationFactory) {
    this.authotizationAccessor = authorizationAccessor;
    this.backgroundSync = backgroundSync;
    this.authorizationFactory = authorizationFactory;
  }

  public Completable createPaymentAuthorization(int paymentId) {
    return CreatePaymentAuthorizationRequest.of(AptoideAccountManager.getAccessToken(), paymentId)
        .observe()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Observable.just(null);
          }
          return Observable.<Void>error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        })
        .toCompletable();
  }


  public Observable<Authorization> getPaymentAuthorization(int paymentId) {
    return getPaymentAuthorizations(Collections.singletonList(paymentId))
        .flatMapIterable(authorizations -> authorizations)
        .filter(authorization -> authorization.getPaymentId() == paymentId);
  }

  public Observable<List<Authorization>> getPaymentAuthorizations(List<Integer> paymentIds) {
    return syncAuthorizations(paymentIds).andThen(authotizationAccessor.getPaymentAuthorizations()
        .flatMap(paymentAuthorizations -> Observable.from(paymentAuthorizations)
            .map(paymentAuthorization -> authorizationFactory.convertToPaymentAuthorization(
                paymentAuthorization))
            .toList()));
  }

  private Completable syncAuthorizations(List<Integer> paymentIds) {
    return Observable.from(paymentIds)
        .doOnNext(paymentId -> authotizationAccessor.save(
            authorizationFactory.convertToDatabasePaymentAuthorization(
                authorizationFactory.create(paymentId, Authorization.Status.UNKNOWN))))
        .map(paymentId -> String.valueOf(paymentId))
        .toList()
        .doOnNext(stringIds -> backgroundSync.syncAuthorizations(stringIds))
        .toCompletable();
  }
}