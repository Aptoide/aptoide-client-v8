/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.BillingSyncScheduler;
import cm.aptoide.pt.billing.Payer;
import rx.Observable;
import rx.Single;

public class AuthorizationRepository {

  private final BillingSyncScheduler syncScheduler;
  private final Payer payer;
  private final AuthorizationService authorizationService;
  private final AuthorizationPersistence authorizationPersistence;

  public AuthorizationRepository(BillingSyncScheduler syncScheduler, Payer payer,
      AuthorizationService authorizationService,
      AuthorizationPersistence authorizationPersistence) {
    this.authorizationPersistence = authorizationPersistence;
    this.syncScheduler = syncScheduler;
    this.authorizationService = authorizationService;
    this.payer = payer;
  }

  public Single<Authorization> createAuthorization(int paymentMethodId) {
    return payer.getId()
        .flatMap(payerId -> authorizationService.createAuthorization(payerId, paymentMethodId)
            .flatMap(authorization -> authorizationPersistence.saveAuthorization(authorization)
                .andThen(Single.just(authorization))));
  }

  public Observable<Authorization> getAuthorization(int paymentMethodId) {
    return payer.getId()
        .doOnSuccess(__ -> syncScheduler.syncAuthorization(paymentMethodId))
        .flatMapObservable(
            payerId -> authorizationPersistence.getAuthorization(payerId, paymentMethodId));
  }

  public Single<Authorization> createAuthorization(int paymentMethodId,
      Authorization.Status status) {
    return payer.getId()
        .flatMap(payerId -> authorizationPersistence.createAuthorization(payerId, paymentMethodId,
            status));
  }
}