/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.BillingSyncScheduler;
import cm.aptoide.pt.billing.Customer;
import rx.Observable;
import rx.Single;

public class AuthorizationRepository {

  private final BillingSyncScheduler syncScheduler;
  private final Customer customer;
  private final AuthorizationService authorizationService;
  private final AuthorizationPersistence authorizationPersistence;

  public AuthorizationRepository(BillingSyncScheduler syncScheduler, Customer customer,
      AuthorizationService authorizationService,
      AuthorizationPersistence authorizationPersistence) {
    this.authorizationPersistence = authorizationPersistence;
    this.syncScheduler = syncScheduler;
    this.authorizationService = authorizationService;
    this.customer = customer;
  }

  public Single<Authorization> createAuthorization(int paymentMethodId) {
    return customer.getId()
        .flatMap(customerId -> authorizationService.createAuthorization(customerId, paymentMethodId)
            .flatMap(authorization -> authorizationPersistence.saveAuthorization(authorization)
                .andThen(Single.just(authorization))));
  }

  public Observable<Authorization> getAuthorization(int paymentMethodId) {
    return customer.getId()
        .doOnSuccess(__ -> syncScheduler.syncAuthorization(paymentMethodId))
        .flatMapObservable(
            customerId -> authorizationPersistence.getAuthorization(customerId, paymentMethodId));
  }

  public Single<Authorization> createAuthorization(int paymentMethodId,
      Authorization.Status status) {
    return customer.getId()
        .flatMap(customerId -> authorizationPersistence.createAuthorization(customerId, paymentMethodId,
            status));
  }
}