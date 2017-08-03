/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.billing.sync;

import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.authorization.Authorization;
import cm.aptoide.pt.v8engine.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.v8engine.billing.authorization.AuthorizationService;
import cm.aptoide.pt.v8engine.sync.Sync;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class AuthorizationSync extends Sync {

  private final int paymentId;
  private final Payer payer;
  private final BillingAnalytics billingAnalytics;
  private final AuthorizationService authorizationService;
  private final AuthorizationPersistence authorizationPersistence;

  public AuthorizationSync(int paymentId, Payer payer, BillingAnalytics billingAnalytics,
      AuthorizationService authorizationService, AuthorizationPersistence authorizationPersistence,
      boolean periodic, boolean exact, long interval, long trigger) {
    super(String.valueOf(paymentId), periodic, exact, trigger, interval);
    this.paymentId = paymentId;
    this.payer = payer;
    this.billingAnalytics = billingAnalytics;
    this.authorizationService = authorizationService;
    this.authorizationPersistence = authorizationPersistence;
  }

  @Override public Completable execute() {
    return payer.getId()
        .flatMapCompletable(payerId -> authorizationService.getAuthorizations(payerId, paymentId)
            .flatMap(authorizations -> saveAuthorizations(payerId, authorizations))
            .doOnSuccess(authorizations -> sendAuthorizationAnalytics(authorizations))
            .doOnError(throwable -> billingAnalytics.sendPaymentAuthorizationErrorEvent(throwable))
            .toCompletable());
  }

  private Single<List<Authorization>> saveAuthorizations(String payerId,
      List<Authorization> authorizations) {
    return createLocalAuthorization(payerId, authorizations).andThen(
        authorizationPersistence.saveAuthorizations(authorizations))
        .andThen(Single.just(authorizations));
  }

  private Completable createLocalAuthorization(String payerId, List<Authorization> authorizations) {
    return Observable.from(authorizations)
        .filter(authorization -> authorization.getPaymentId() == paymentId)
        .switchIfEmpty(authorizationPersistence.createAuthorization(payerId, paymentId,
            Authorization.Status.INACTIVE)
            .toObservable())
        .toCompletable();
  }

  private void sendAuthorizationAnalytics(List<Authorization> authorizations) {
    for (Authorization authorization : authorizations) {
      billingAnalytics.sendAuthorizationCompleteEvent(authorization);
    }
  }
}