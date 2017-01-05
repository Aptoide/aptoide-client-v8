/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository.sync;

import android.content.SyncResult;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.dataprovider.ws.v3.GetPaymentAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.model.v3.PaymentAuthorizationResponse;
import cm.aptoide.pt.v8engine.payment.authorizations.WebAuthorization;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationConverter;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.io.IOException;
import java.util.List;
import rx.Single;

/**
 * Created by marcelobenites on 22/11/16.
 */
public class PaymentAuthorizationSync extends RepositorySync {

  private final int paymentId;
  private final PaymentAuthorizationRepository authorizationRepository;
  private final PaymentAuthorizationAccessor authorizationAccessor;
  private final PaymentAuthorizationConverter authorizationConverter;

  public PaymentAuthorizationSync(int paymentId, PaymentAuthorizationRepository authorizationRepository,
      PaymentAuthorizationAccessor authorizationAccessor,
      PaymentAuthorizationConverter authorizationConverter) {
    this.paymentId = paymentId;
    this.authorizationRepository = authorizationRepository;
    this.authorizationAccessor = authorizationAccessor;
    this.authorizationConverter = authorizationConverter;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      getServerAuthorization(paymentId)
          .doOnSuccess(response -> saveAndReschedulePendingAuthorization(response, syncResult))
          .onErrorReturn(throwable -> {
            saveAndRescheduleOnNetworkError(syncResult, throwable);
            return null;
          }).toBlocking().value();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
  }

  private void saveAndRescheduleOnNetworkError(SyncResult syncResult, Throwable throwable) {
    if (throwable instanceof IOException) {
      rescheduleSync(syncResult);
    } else {
      authorizationAccessor.save(authorizationConverter.convertToDatabasePaymentAuthorization(
          WebAuthorization.syncingError(paymentId)));
    }
  }

  private void saveAndReschedulePendingAuthorization(PaymentAuthorizationResponse response,
      SyncResult syncResult) {
    final cm.aptoide.pt.v8engine.payment.Authorization paymentAuthorization =
        authorizationConverter.convertToPaymentAuthorization(paymentId, response);
    authorizationAccessor.save(
        authorizationConverter.convertToDatabasePaymentAuthorization(paymentId, response));
    if (paymentAuthorization.isPending()) {
      rescheduleSync(syncResult);
    }
  }

  private Single<PaymentAuthorizationResponse> getServerAuthorization(int paymentId) {
    return GetPaymentAuthorizationRequest.of(paymentId, AptoideAccountManager.getAccessToken())
        .observe()
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(response);
          }
          return Single.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
        });
  }
}