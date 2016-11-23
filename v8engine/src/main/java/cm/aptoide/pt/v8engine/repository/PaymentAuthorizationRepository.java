/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.dataprovider.ws.v3.GetProductPurchaseAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.v8engine.payment.PaymentAuthorization;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import rx.Observable;

public class PaymentAuthorizationRepository implements Repository {
  private final PaymentAuthorizationAccessor authotizationAccessor;

  public PaymentAuthorizationRepository(PaymentAuthorizationAccessor authotizationAccessor) {
    this.authotizationAccessor = authotizationAccessor;
  }

  public Observable<PaymentAuthorization> getPaymentAuthorization(int paymentId) {
    return syncDatabaseAuthorizationWithServer(paymentId)
        .flatMap(paymentAuthorization -> authotizationAccessor.getPaymentAuthorization(paymentId)
            .flatMap(databaseAuthorization -> {
              if (databaseAuthorization != null) {
                return Observable.just(convertToPaymentAuthorization(databaseAuthorization));
              }
              return Observable.error(new RepositoryItemNotFoundException(
                  "No payment authorization found for payment id: " + paymentId));
            }));
  }

  public Observable<Void> removePaymentAuthorization(int paymentId) {
    return Observable.fromCallable(() -> {
      authotizationAccessor.deleteAuthorization(paymentId);
      return null;
    });
  }

  public Observable<List<PaymentAuthorization>> getPaymentAuthorizations() {
    return getDatabasePaymentAuthorizations().first()
        .flatMapIterable(paymentAuthorizations -> paymentAuthorizations)
        .flatMap(paymentAuthorization -> syncDatabaseAuthorizationWithServer(
            paymentAuthorization.getPaymentId()))
        .toList()
        .flatMap(synced -> getDatabasePaymentAuthorizations());
  }

  private Observable<Void> syncDatabaseAuthorizationWithServer(int paymentId) {
    return GetProductPurchaseAuthorizationRequest.of(AptoideAccountManager.getAccessToken(), paymentId)
        .observe()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Observable.just(
                new PaymentAuthorization(paymentId, response.getUrl(), response.getSuccessUrl(),
                    PaymentAuthorization.Status.valueOf(response.getAuthorizationStatus())));
          }
          return Observable.<PaymentAuthorization>error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        })
        .<Void>flatMap(serverAuthorization -> Observable.fromCallable(() -> {
          authotizationAccessor.save(new cm.aptoide.pt.database.realm.PaymentAuthorization(
              serverAuthorization.getPaymentId(), serverAuthorization.getUrl(),
              serverAuthorization.getRedirectUrl(), serverAuthorization.getStatus().name()));
          return null;
        }))
        .onErrorReturn(throwable -> null);
  }

  private Observable<List<PaymentAuthorization>> getDatabasePaymentAuthorizations() {
    return authotizationAccessor.getPaymentAuthorizations()
        .flatMap(paymentAuthorizations -> Observable.from(paymentAuthorizations)
            .map(paymentAuthorization -> convertToPaymentAuthorization(paymentAuthorization))
            .toList());
  }

  private PaymentAuthorization convertToPaymentAuthorization(
      cm.aptoide.pt.database.realm.PaymentAuthorization paymentAuthorization) {
    return new PaymentAuthorization(paymentAuthorization.getPaymentId(),
        paymentAuthorization.getUrl(), paymentAuthorization.getRedirectUrl(),
        PaymentAuthorization.Status.valueOf(paymentAuthorization.getStatus()));
  }
}