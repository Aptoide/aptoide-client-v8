/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePurchaseAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetProductPurchaseAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.model.v3.PurchaseAuthorizationResponse;
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
        .flatMap(response -> savePaymentAuthorizationInDatabase(paymentId, response))
        .flatMap(saved -> getDatabasePaymentAuthorization(paymentId));
  }

  public Observable<PaymentAuthorization> getPaymentAuthorization(int paymentId) {
    return syncServerDatabaseAuthorization(paymentId).flatMap(
        synced -> getDatabasePaymentAuthorization(paymentId));
  }

  public Observable<List<PaymentAuthorization>> getPaymentAuthorizations() {
    return authotizationAccessor.getPaymentAuthorizations()
        .first()
        .flatMapIterable(paymentAuthorizations -> paymentAuthorizations)
        .flatMap(paymentAuthorization -> syncServerDatabaseAuthorization(
            paymentAuthorization.getPaymentId()))
        .toList()
        .flatMap(synced -> authotizationAccessor.getPaymentAuthorizations()
            .flatMap(paymentAuthorizations -> Observable.from(paymentAuthorizations)
                .map(paymentAuthorization -> convertToPaymentAuthorization(paymentAuthorization))
                .toList()));
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
            return Observable.just(convertToPaymentAuthorization(databaseAuthorization));
          }
          return Observable.error(new RepositoryItemNotFoundException(
              "No payment authorization found for payment id: " + paymentId));
        });
  }

  private Observable<Void> syncServerDatabaseAuthorization(int paymentId) {
    return GetProductPurchaseAuthorizationRequest.of(AptoideAccountManager.getAccessToken(),
        paymentId).observe().flatMap(response -> {
      if (response != null && response.isOk()) {
        return savePaymentAuthorizationInDatabase(paymentId, response);
      }
      return removePaymentAuthorization(paymentId);
    });
  }

  private Observable<Void> savePaymentAuthorizationInDatabase(int paymentId,
      PurchaseAuthorizationResponse response) {
    return Observable.fromCallable(() -> {
      authotizationAccessor.save(
          new cm.aptoide.pt.database.realm.PaymentAuthorization(paymentId, response.getUrl(),
              response.getSuccessUrl(), response.getAuthorizationStatus()));
      return null;
    });
  }

  private PaymentAuthorization convertToPaymentAuthorization(
      cm.aptoide.pt.database.realm.PaymentAuthorization paymentAuthorization) {
    return new PaymentAuthorization(paymentAuthorization.getPaymentId(),
        paymentAuthorization.getUrl(), paymentAuthorization.getRedirectUrl(),
        PaymentAuthorization.Status.valueOf(paymentAuthorization.getStatus()));
  }
}