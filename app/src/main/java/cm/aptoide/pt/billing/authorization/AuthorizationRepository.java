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
  private final AuthorizationFactory authorizationFactory;

  public AuthorizationRepository(BillingSyncScheduler syncScheduler, Customer customer,
      AuthorizationPersistence authorizationPersistence,
      AuthorizationFactory authorizationFactory) {
    this.authorizationPersistence = authorizationPersistence;
    this.syncScheduler = syncScheduler;
    this.customer = customer;
    this.authorizationFactory = authorizationFactory;
  }

  public Observable<Authorization> getAuthorization(String transactionId) {
    return customer.getId()
        .doOnSuccess(__ -> syncScheduler.syncAuthorization(transactionId))
        .flatMapObservable(
            customerId -> authorizationPersistence.getAuthorization(customerId, transactionId));
  }

  public Completable updateAuthorization(String transactionId, String metadata) {
    return customer.getId()
        .flatMap(
            customerId -> authorizationPersistence.updateAuthorization(customerId, transactionId,
                Authorization.Status.PENDING_SYNC, metadata))
        .doOnSuccess(
            authorization -> syncScheduler.syncAuthorization(authorization.getTransactionId()))
        .toCompletable();
  }
}