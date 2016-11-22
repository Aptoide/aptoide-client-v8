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
import cm.aptoide.pt.model.v3.GetProductPurchaseAuthorizationResponse;
import cm.aptoide.pt.v8engine.payment.PaymentAuthorization;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PaymentAuthorizationRepository implements Repository {
  private final PaymentAuthorizationAccessor authotizationAccessor;

  public PaymentAuthorizationRepository(PaymentAuthorizationAccessor authotizationAccessor) {
    this.authotizationAccessor = authotizationAccessor;
  }

  public Observable<PaymentAuthorization> createPaymentAuthorization(int paymentId) {
    return createOrGetServerPaymentAuthorization(paymentId);
  }

  public Observable<PaymentAuthorization> getPaymentAuthorization(int paymentId) {
    return getDatabasePaymentAuthorization(paymentId).flatMap(
        paymentAuthorization -> updatePaymentAuthorizationWithServerStatus(paymentAuthorization));
  }

  public Observable<Void> removePaymentAuthorization(int paymentId) {
    return deleteStoredPaymentAuthorization(paymentId);
  }

  public Observable<List<PaymentAuthorization>> getPaymentAuthorizations() {
    return getDatabasePaymentAuthorizations().flatMap(
        paymentAuthorizations -> Observable.from(paymentAuthorizations)
            .flatMap(paymentAuthorization -> updatePaymentAuthorizationWithServerStatus(
                paymentAuthorization))
            .toList());
  }

  public Observable<Void> savePaymentAuthorization(PaymentAuthorization paymentAuthorization) {
    return storePaymentAuthorizationInDatabase(paymentAuthorization).subscribeOn(Schedulers.io());
  }

  private Observable<PaymentAuthorization> updatePaymentAuthorizationWithServerStatus(
      PaymentAuthorization paymentAuthorization) {
    return createOrGetServerPaymentAuthorization(paymentAuthorization.getPaymentId()).onErrorReturn(
        throwable -> paymentAuthorization);
  }

  private Observable<PaymentAuthorization> createOrGetServerPaymentAuthorization(int paymentId) {
    return GetProductPurchaseAuthorizationRequest.of(AptoideAccountManager.getAccessToken(),
        paymentId).observe().flatMap(response -> {
      if (response != null && response.isOk()) {
        return Observable.just(
            new PaymentAuthorization(paymentId, response.getUrl(), response.getSuccessUrl(),
                response.getAuthorizationStatus()));
      }
      return Observable.<PaymentAuthorization>error(
          new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
    });
  }

  private Observable<Void> deleteStoredPaymentAuthorization(int paymentId) {
    return Observable.fromCallable(() -> {
      authotizationAccessor.deleteAuthorization(paymentId);
      return null;
    });
  }

  private Observable<Void> storePaymentAuthorizationInDatabase(PaymentAuthorization paymentAuthorization) {
    return Observable.fromCallable(() -> {
      authotizationAccessor.save(convertToStoredPaymentAuthorization(paymentAuthorization));
      return null;
    });
  }

  private cm.aptoide.pt.database.realm.PaymentAuthorization convertToStoredPaymentAuthorization(
      PaymentAuthorization paymentAuthorization) {
    return new cm.aptoide.pt.database.realm.PaymentAuthorization(
        paymentAuthorization.getPaymentId(), paymentAuthorization.getUrl(),
        paymentAuthorization.getRedirectUrl(), paymentAuthorization.getStatus().name());
  }

  private Observable<PaymentAuthorization> getDatabasePaymentAuthorization(int paymentId) {
    return authotizationAccessor.getPaymentAuthorization(paymentId).flatMap(paymentAuthorization -> {
      if (paymentAuthorization != null) {
        return Observable.just(convertToPaymentAuthorization(paymentAuthorization));
      }
      return Observable.error(new RepositoryItemNotFoundException(
          "No payment authorization found for payment id: " + paymentId));
    });
  }

  private @NonNull PaymentAuthorization convertToPaymentAuthorization(
      cm.aptoide.pt.database.realm.PaymentAuthorization paymentAuthorization) {
    return new PaymentAuthorization(paymentAuthorization.getPaymentId(),
        paymentAuthorization.getUrl(), paymentAuthorization.getRedirectUrl(),
        GetProductPurchaseAuthorizationResponse.Status.valueOf(paymentAuthorization.getStatus()));
  }

  private Observable<List<PaymentAuthorization>> getDatabasePaymentAuthorizations() {
    return authotizationAccessor.getPaymentAuthorizations()
        .flatMap(paymentAuthorizations -> Observable.from(paymentAuthorizations)
            .map(paymentAuthorization -> convertToPaymentAuthorization(paymentAuthorization))
            .toList());
  }
}