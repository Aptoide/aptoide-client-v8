/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository.sync;

import android.content.SyncResult;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.database.realm.PaymentAuthorization;
import cm.aptoide.pt.dataprovider.ws.v3.GetPaymentAuthorizationsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.model.v3.PaymentAuthorizationsResponse;
import cm.aptoide.pt.v8engine.payment.Authorization;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import rx.Single;

/**
 * Created by marcelobenites on 22/11/16.
 */
public class PaymentAuthorizationSync extends RepositorySync {

  private final List<String> paymentIds;
  private final PaymentAuthorizationAccessor authorizationAccessor;
  private final PaymentAuthorizationFactory authorizationFactory;
  private final AptoideAccountManager accountManager;

  public PaymentAuthorizationSync(List<String> paymentIds,
      PaymentAuthorizationAccessor authorizationAccessor,
      PaymentAuthorizationFactory authorizationFactory, AptoideAccountManager accountManager) {
    this.paymentIds = paymentIds;
    this.authorizationAccessor = authorizationAccessor;
    this.authorizationFactory = authorizationFactory;
    this.accountManager = accountManager;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      final String accessToken = accountManager.getAccessToken();
      final String payerId = accountManager.getAccountEmail();
      getServerAuthorizations(accessToken).doOnSuccess(
          response -> saveAndReschedulePendingAuthorization(response, syncResult, paymentIds,
              payerId)).onErrorReturn(throwable -> {
        saveAndRescheduleOnNetworkError(syncResult, throwable, paymentIds, payerId);
        return null;
      }).toBlocking().value();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
  }

  private Single<List<PaymentAuthorizationsResponse.PaymentAuthorizationResponse>> getServerAuthorizations(
      String accessToken) {
    return GetPaymentAuthorizationsRequest.of(accessToken)
        .observe()
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(response.getAuthorizations());
          }
          return Single.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
        });
  }

  private void saveAndReschedulePendingAuthorization(
      List<PaymentAuthorizationsResponse.PaymentAuthorizationResponse> responses,
      SyncResult syncResult, List<String> paymentIds, String payerId) {

    final List<PaymentAuthorization> authorizations = new ArrayList<>();
    final List<String> paymentIdsCopy = new ArrayList<>(paymentIds);

    for (PaymentAuthorizationsResponse.PaymentAuthorizationResponse response : responses) {
      final cm.aptoide.pt.v8engine.payment.Authorization paymentAuthorization =
          authorizationFactory.convertToPaymentAuthorization(response, payerId);
      authorizations.add(
          authorizationFactory.convertToDatabasePaymentAuthorization(response, payerId));
      if (paymentAuthorization.isPending() || paymentAuthorization.isInitiated()) {
        rescheduleSync(syncResult);
      }
      paymentIdsCopy.remove(String.valueOf(response.getPaymentId()));
    }

    for (String paymentId : paymentIdsCopy) {
      authorizations.add(authorizationFactory.convertToDatabasePaymentAuthorization(
          authorizationFactory.create(Integer.valueOf(paymentId), Authorization.Status.INACTIVE,
              payerId)));
    }

    authorizationAccessor.updateAll(authorizations);
  }

  private void saveAndRescheduleOnNetworkError(SyncResult syncResult, Throwable throwable,
      List<String> paymentIds, String payerId) {
    if (throwable instanceof IOException) {
      rescheduleSync(syncResult);
    } else {
      final List<PaymentAuthorization> authorizations = new ArrayList<>();
      for (String paymentId : paymentIds) {
        authorizations.add(authorizationFactory.convertToDatabasePaymentAuthorization(
            authorizationFactory.create(Integer.valueOf(paymentId), Authorization.Status.INACTIVE,
                payerId)));
      }
      authorizationAccessor.updateAll(authorizations);
    }
  }
}