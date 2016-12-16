/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePurchaseAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.v8engine.payment.PaymentAuthorization;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import cm.aptoide.pt.v8engine.repository.sync.SyncAdapterBackgroundSync;
import rx.Observable;

public class PaymentAuthorizationRepository implements Repository {
  private final PaymentAuthorizationAccessor authotizationAccessor;
  private final SyncAdapterBackgroundSync backgroundSync;
  private final PaymentAuthorizationConverter paymentAuthorizationConverter;

  public PaymentAuthorizationRepository(PaymentAuthorizationAccessor authotizationAccessor,
      SyncAdapterBackgroundSync backgroundSync,
      PaymentAuthorizationConverter paymentAuthorizationConverter) {
    this.authotizationAccessor = authotizationAccessor;
    this.backgroundSync = backgroundSync;
    this.paymentAuthorizationConverter = paymentAuthorizationConverter;
  }

  public Observable<PaymentAuthorization> createPaymentAuthorization(int paymentId) {
    return CreatePurchaseAuthorizationRequest.of(AptoideAccountManager.getAccessToken(), paymentId)
        .observe()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Observable.just(response);
          }
          return Observable.error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        })
        .map(response -> paymentAuthorizationConverter.convertToPaymentAuthorization(paymentId,
            response))
        .doOnNext(
            paymentAuthorization -> syncPaymentAuthorizationInBackground(paymentAuthorization))
        .flatMap(saved -> getDatabasePaymentAuthorization(paymentId));
  }

  public Observable<PaymentAuthorization> getPaymentAuthorization(int paymentId) {
    return getDatabasePaymentAuthorization(paymentId);
  }

  public Observable<Void> removePaymentAuthorization(int paymentId) {
    return Observable.fromCallable(() -> {
      authotizationAccessor.deleteAuthorization(paymentId);
      return null;
    });
  }

  private Observable<PaymentAuthorization> getDatabasePaymentAuthorization(int paymentId) {
    return authotizationAccessor.getPaymentAuthorization(paymentId)
        .flatMap(databaseAuthorization -> {
          if (databaseAuthorization != null) {
            return Observable.just(
                paymentAuthorizationConverter.convertToPaymentAuthorization(databaseAuthorization));
          }
          return Observable.error(new RepositoryItemNotFoundException(
              "No payment authorization found for payment id: " + paymentId));
        });
  }

  private void syncPaymentAuthorizationInBackground(PaymentAuthorization paymentAuthorization) {
    authotizationAccessor.save(
        paymentAuthorizationConverter.convertToPaymentAuthorization(paymentAuthorization));
    backgroundSync.syncAuthorization(paymentAuthorization.getPaymentId());
  }
}