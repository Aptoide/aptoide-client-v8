/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.BillingSyncScheduler;
import cm.aptoide.pt.billing.Customer;
import rx.Completable;
import rx.Observable;

public class AuthorizationRepository {

  private final BillingSyncScheduler syncScheduler;
  private final Customer customer;
  private final AuthorizationPersistence authorizationPersistence;

  public AuthorizationRepository(BillingSyncScheduler syncScheduler, Customer customer,
      AuthorizationPersistence authorizationPersistence) {
    this.authorizationPersistence = authorizationPersistence;
    this.syncScheduler = syncScheduler;
    this.customer = customer;
  }

  public Completable updateAuthorization(Authorization authorization) {
    return customer.getId()
        .flatMapCompletable(customerId -> authorizationPersistence.saveAuthorization(authorization))
        .doOnCompleted(() -> syncScheduler.syncAuthorization(authorization.getTransactionId()));
  }

  public Observable<Authorization> getAuthorization(String transactionId) {
    return customer.getId()
        .doOnSuccess(__ -> syncScheduler.syncAuthorization(transactionId))
        .flatMapObservable(
            customerId -> authorizationPersistence.getAuthorization(customerId, transactionId));
  }
}