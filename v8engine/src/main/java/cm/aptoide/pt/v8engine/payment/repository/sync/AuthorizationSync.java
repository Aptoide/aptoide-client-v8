/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.repository.sync;

import android.content.SyncResult;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.GetPaymentAuthorizationsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.v8engine.payment.Authorization;
import cm.aptoide.pt.v8engine.payment.Payer;
import cm.aptoide.pt.v8engine.payment.PaymentAnalytics;
import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import cm.aptoide.pt.v8engine.sync.RepositorySync;
import java.io.IOException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

public class AuthorizationSync extends RepositorySync {

  private final int paymentId;
  private final PaymentAuthorizationAccessor authorizationAccessor;
  private final PaymentAuthorizationFactory authorizationFactory;
  private final Payer payer;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final PaymentAnalytics paymentAnalytics;

  public AuthorizationSync(int paymentId, PaymentAuthorizationAccessor authorizationAccessor,
      PaymentAuthorizationFactory authorizationFactory, Payer payer,
      BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory, PaymentAnalytics paymentAnalytics) {
    this.paymentId = paymentId;
    this.authorizationAccessor = authorizationAccessor;
    this.authorizationFactory = authorizationFactory;
    this.payer = payer;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.paymentAnalytics = paymentAnalytics;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      payer.getId()
          .flatMap(payerId -> getServerAuthorization(payerId).doOnSuccess(
              authorization -> saveAndReschedulePendingAuthorization(authorization, syncResult,
                  payerId)).onErrorReturn(throwable -> {
            saveAndRescheduleOnNetworkError(syncResult, throwable, payerId);
            return null;
          }))
          .toBlocking()
          .value();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
  }

  private Single<Authorization> getServerAuthorization(String payerId) {
    return GetPaymentAuthorizationsRequest.of(bodyInterceptorV3, httpClient, converterFactory)
        .observe()
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Observable.from(response.getAuthorizations())
                .filter(authorization -> authorization.getPaymentId() == paymentId)
                .map(authorization -> authorizationFactory.convertToPaymentAuthorization(
                    authorization, payerId))
                .first()
                .toSingle();
          }
          return Single.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
        });
  }

  private void saveAndReschedulePendingAuthorization(Authorization authorization,
      SyncResult syncResult, String payerId) {

    if (authorization.isPending() || authorization.isPendingUserConsent()) {
      rescheduleSync(syncResult);
    }

    authorizationAccessor.save(
        authorizationFactory.convertToDatabasePaymentAuthorization(authorization));
    paymentAnalytics.sendAuthorizationCompleteEvent(authorization);
  }

  private void saveAndRescheduleOnNetworkError(SyncResult syncResult, Throwable throwable,
      String payerId) {
    if (throwable instanceof IOException) {
      paymentAnalytics.sendPaymentAuthorizationNetworkRetryEvent();
      rescheduleSync(syncResult);
    } else {
      authorizationAccessor.save(authorizationFactory.convertToDatabasePaymentAuthorization(
          authorizationFactory.create(Integer.valueOf(paymentId), Authorization.Status.INACTIVE,
              payerId)));
    }
  }
}