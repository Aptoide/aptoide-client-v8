/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository.sync;

import android.content.SyncResult;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.dataprovider.ws.v3.GetPaymentAuthorizationsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.model.v3.PaymentAuthorizationsResponse;
import cm.aptoide.pt.v8engine.payment.authorizations.WebAuthorization;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.io.IOException;
import java.util.List;
import rx.Single;

/**
 * Created by marcelobenites on 22/11/16.
 */
public class PaymentAuthorizationSync extends RepositorySync {

  private final List<String> paymentIds;
  private final PaymentAuthorizationRepository authorizationRepository;
  private final PaymentAuthorizationAccessor authorizationAccessor;
  private final PaymentAuthorizationFactory authorizationFactory;

  public PaymentAuthorizationSync(List<String> paymentIds,
      PaymentAuthorizationRepository authorizationRepository,
      PaymentAuthorizationAccessor authorizationAccessor,
      PaymentAuthorizationFactory authorizationFactory) {
    this.paymentIds = paymentIds;
    this.authorizationRepository = authorizationRepository;
    this.authorizationAccessor = authorizationAccessor;
    this.authorizationFactory = authorizationFactory;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      getServerAuthorizations().doOnSuccess(
          response -> saveAndReschedulePendingAuthorization(response, syncResult))
          .onErrorReturn(throwable -> {
            saveAndRescheduleOnNetworkError(syncResult, throwable, paymentIds);
            return null;
          })
          .toBlocking()
          .value();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
  }

  private void saveAndRescheduleOnNetworkError(SyncResult syncResult, Throwable throwable,
      List<String> paymentIds) {
    if (throwable instanceof IOException) {
      rescheduleSync(syncResult);
    } else {
      for (String paymentId : paymentIds) {
        authorizationAccessor.save(authorizationFactory.convertToDatabasePaymentAuthorization(
            authorizationFactory.syncingError(Integer.valueOf(paymentId))));
      }
    }
  }

  private void saveAndReschedulePendingAuthorization(List<PaymentAuthorizationsResponse.PaymentAuthorizationResponse> responses,
      SyncResult syncResult) {
    for (PaymentAuthorizationsResponse.PaymentAuthorizationResponse response : responses) {
      final cm.aptoide.pt.v8engine.payment.Authorization paymentAuthorization =
          authorizationFactory.convertToPaymentAuthorization(response);
      authorizationAccessor.save(
          authorizationFactory.convertToDatabasePaymentAuthorization(response));
      if (paymentAuthorization.isPending()) {
        rescheduleSync(syncResult);
      }
    }
  }

  private Single<List<PaymentAuthorizationsResponse.PaymentAuthorizationResponse>> getServerAuthorizations() {
    return GetPaymentAuthorizationsRequest.of(AptoideAccountManager.getAccessToken())
        .observe()
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(response.getAuthorizations());
          }
          return Single.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
        });
  }
}