/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository.sync;

import android.content.SyncResult;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.dataprovider.ws.v3.GetProductPurchaseAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.model.v3.PurchaseAuthorizationResponse;
import cm.aptoide.pt.v8engine.payment.PaymentAuthorization;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationConverter;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.io.IOException;
import rx.Single;

/**
 * Created by marcelobenites on 22/11/16.
 */
public class PaymentAuthorizationSync extends RepositorySync {

  private final PaymentAuthorizationRepository authorizationRepository;
  private final int paymentId;
  private final PaymentAuthorizationAccessor authorizationAccessor;
  private final PaymentAuthorizationConverter authorizationConverter;

  public PaymentAuthorizationSync(PaymentAuthorizationRepository authorizationRepository,
      int paymentId, PaymentAuthorizationAccessor authorizationAccessor,
      PaymentAuthorizationConverter authorizationConverter) {
    this.authorizationRepository = authorizationRepository;
    this.paymentId = paymentId;
    this.authorizationAccessor = authorizationAccessor;
    this.authorizationConverter = authorizationConverter;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      getServerAuthorization(paymentId).doOnSuccess(
          response -> saveAndReschedulePendingAuthorization(response, syncResult))
          .onErrorReturn(throwable -> {
            saveAndRescheduleOnNetworkError(syncResult, throwable);
            return null;
          })
          .toBlocking()
          .value();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
  }

  private void saveAndRescheduleOnNetworkError(SyncResult syncResult, Throwable throwable) {
    if (throwable instanceof IOException) {
      rescheduleSync(syncResult);
    } else {
      authorizationAccessor.save(authorizationConverter.convertToDatabasePaymentAuthorization(
          PaymentAuthorization.syncingError(paymentId)));
    }
  }

  private void saveAndReschedulePendingAuthorization(PurchaseAuthorizationResponse response,
      SyncResult syncResult) {
    final PaymentAuthorization paymentAuthorization =
        authorizationConverter.convertToPaymentAuthorization(paymentId, response);
    authorizationAccessor.save(
        authorizationConverter.convertToDatabasePaymentAuthorization(paymentId, response));
    if (paymentAuthorization.isPending()) {
      rescheduleSync(syncResult);
    }
  }

  private Single<PurchaseAuthorizationResponse> getServerAuthorization(int paymentId) {
    return GetProductPurchaseAuthorizationRequest.of(AptoideAccountManager.getAccessToken(),
        paymentId).observe().toSingle().flatMap(response -> {
      if (response != null && response.isOk()) {
        return Single.just(response);
      }
      return Single.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
    });
  }

  private void reschedulePendingAuthorization(PaymentAuthorization paymentAuthorization,
      SyncResult syncResult) {
    if (paymentAuthorization.isPending()) {
      rescheduleSync(syncResult);
    }
  }
}