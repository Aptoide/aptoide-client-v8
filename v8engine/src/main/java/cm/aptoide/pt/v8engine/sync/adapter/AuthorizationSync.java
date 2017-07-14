/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync.adapter;

import android.content.SyncResult;
import cm.aptoide.pt.v8engine.billing.Authorization;
import cm.aptoide.pt.v8engine.billing.AuthorizationPersistence;
import cm.aptoide.pt.v8engine.billing.AuthorizationService;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.Payer;
import java.io.IOException;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class AuthorizationSync extends ScheduledSync {

  private final int paymentId;
  private final Payer payer;
  private final BillingAnalytics billingAnalytics;
  private final AuthorizationService authorizationService;
  private final AuthorizationPersistence authorizationPersistence;

  public AuthorizationSync(int paymentId, Payer payer, BillingAnalytics billingAnalytics,
      AuthorizationService authorizationService,
      AuthorizationPersistence authorizationPersistence) {
    this.paymentId = paymentId;
    this.payer = payer;
    this.billingAnalytics = billingAnalytics;
    this.authorizationService = authorizationService;
    this.authorizationPersistence = authorizationPersistence;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      payer.getId()
          .flatMapCompletable(payerId -> authorizationService.getAuthorizations(payerId, paymentId)
              .flatMap(authorizations -> saveAuthorizations(payerId, authorizations))
              .doOnSuccess(
                  authorizations -> reschedulePendingAuthorizations(authorizations, syncResult))
              .doOnError(throwable -> rescheduleOnNetworkError(syncResult, throwable))
              .toCompletable()
              .onErrorComplete())
          .await();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
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
        .switchIfEmpty(authorizationPersistence.createAuthorization(paymentId, payerId,
            Authorization.Status.INACTIVE)
            .toObservable())
        .toCompletable();
  }

  private void reschedulePendingAuthorizations(List<Authorization> authorizations,
      SyncResult syncResult) {

    boolean reschedule = false;
    for (Authorization authorization : authorizations) {

      if (authorization.isPending()) {
        reschedule = true;
      }

      billingAnalytics.sendAuthorizationCompleteEvent(authorization);
    }

    if (reschedule) {
      rescheduleSync(syncResult);
    }
  }

  private void rescheduleOnNetworkError(SyncResult syncResult, Throwable throwable) {
    if (throwable instanceof IOException) {
      billingAnalytics.sendPaymentAuthorizationNetworkRetryEvent();
      rescheduleSync(syncResult);
    }
  }
}