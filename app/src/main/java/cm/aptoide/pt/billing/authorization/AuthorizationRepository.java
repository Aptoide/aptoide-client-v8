/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.BillingSyncScheduler;
import cm.aptoide.pt.billing.Customer;
import rx.Completable;
import rx.Observable;
import rx.Single;

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

  public Completable updateAuthorization(String authorizationId, String metadata,
      Authorization.Status status) {
    return customer.getId()
        .flatMap(
            customerId -> authorizationPersistence.updateAuthorization(customerId, authorizationId,
                status, metadata))
        .toCompletable();
  }

  public Single<Authorization> createAuthorization(String transactionId,
      Authorization.Status status) {
    return customer.getId()
        .flatMap(
            customerId -> authorizationPersistence.createAuthorization(customerId, transactionId,
                status));
  }

  public Completable removeAuthorizations(String transactionId) {
    return customer.getId()
        .doOnSuccess(__ -> syncScheduler.cancelAuthorizationSync(transactionId))
        .flatMapCompletable(
            customerId -> authorizationPersistence.removeAuthorizations(customerId, transactionId));
  }
}