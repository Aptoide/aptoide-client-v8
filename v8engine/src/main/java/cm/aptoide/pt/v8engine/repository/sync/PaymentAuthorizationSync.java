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
import cm.aptoide.pt.v8engine.payment.PaymentAuthorization;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationConverter;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import rx.Observable;

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
      getServerAuthorization(paymentId).doOnNext(paymentAuthorization -> authorizationAccessor.save(
          authorizationConverter.convertToPaymentAuthorization(paymentAuthorization)))
          .onErrorReturn(throwable -> {
            if (throwable instanceof RepositoryItemNotFoundException) {
              authorizationAccessor.deleteAuthorization(paymentId);
            } else {
              rescheduleOrCancelSync(syncResult, throwable);
            }
            return null;
          })
          .toBlocking()
          .subscribe();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
  }

  private Observable<PaymentAuthorization> getServerAuthorization(int paymentId) {
    return GetProductPurchaseAuthorizationRequest.of(AptoideAccountManager.getAccessToken(),
        paymentId).observe().flatMap(response -> {
      if (response != null && response.isOk()) {
        return Observable.just(response);
      }
      return Observable.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
    }).map(response -> authorizationConverter.convertToPaymentAuthorization(paymentId, response));
  }
}