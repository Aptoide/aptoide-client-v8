/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.v8engine.billing.authorization.Authorization;
import cm.aptoide.pt.v8engine.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.v8engine.billing.authorization.AuthorizationService;
import cm.aptoide.pt.v8engine.billing.BillingSyncScheduler;
import cm.aptoide.pt.v8engine.billing.Payer;
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

  public Single<Authorization> createAuthorization(int paymentId) {
    return payer.getId()
        .flatMap(payerId -> authorizationService.createAuthorization(payerId, paymentId)
            .flatMap(authorization -> authorizationPersistence.saveAuthorization(authorization)
                .andThen(Single.just(authorization))));
  }

  public Observable<Authorization> getAuthorization(int paymentId) {
    return payer.getId()
        .flatMapObservable(payerId -> syncScheduler.scheduleAuthorizationSync(paymentId)
            .andThen(authorizationPersistence.getAuthorization(paymentId, payerId)));
  }
}