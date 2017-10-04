/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.billing.sync;

import cm.aptoide.pt.billing.Customer;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.AuthorizationService;
import cm.aptoide.pt.sync.Sync;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class AuthorizationSync extends Sync {

  private final int paymentId;
  private final Customer customer;
  private final AuthorizationService authorizationService;
  private final AuthorizationPersistence authorizationPersistence;

  public AuthorizationSync(int paymentId, Customer customer, AuthorizationService authorizationService,
      AuthorizationPersistence authorizationPersistence, boolean periodic, boolean exact,
      long interval, long trigger) {
    super(String.valueOf(paymentId), periodic, exact, trigger, interval);
    this.paymentId = paymentId;
    this.customer = customer;
    this.authorizationService = authorizationService;
    this.authorizationPersistence = authorizationPersistence;
  }

  @Override public Completable execute() {
    return customer.getId()
        .flatMapCompletable(customerId -> authorizationService.getAuthorizations(customerId, paymentId)
            .flatMap(authorizations -> saveAuthorizations(customerId, authorizations))
            .toCompletable());
  }

  private Single<List<Authorization>> saveAuthorizations(String customerId,
      List<Authorization> authorizations) {
    return createLocalAuthorization(customerId, authorizations).andThen(
        authorizationPersistence.saveAuthorizations(authorizations))
        .andThen(Single.just(authorizations));
  }

  private Completable createLocalAuthorization(String customerId, List<Authorization> authorizations) {
    return Observable.from(authorizations)
        .filter(authorization -> authorization.getPaymentId() == paymentId)
        .switchIfEmpty(authorizationPersistence.createAuthorization(customerId, paymentId,
            Authorization.Status.INACTIVE)
            .toObservable())
        .toCompletable();
  }
}