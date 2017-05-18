/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaymentAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.v8engine.billing.Authorization;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.repository.sync.PaymentSyncScheduler;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;

public class AuthorizationRepository {

  private final PaymentAuthorizationAccessor authotizationAccessor;
  private final PaymentSyncScheduler backgroundSync;
  private final AuthorizationFactory authorizationFactory;
  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final Payer payer;

  public AuthorizationRepository(PaymentAuthorizationAccessor authorizationAccessor,
      PaymentSyncScheduler backgroundSync, AuthorizationFactory authorizationFactory,
      AptoideAccountManager accountManager, BodyInterceptor<BaseBody> bodyInterceptorV3,
      OkHttpClient httpClient, Converter.Factory converterFactory, Payer payer) {
    this.authotizationAccessor = authorizationAccessor;
    this.backgroundSync = backgroundSync;
    this.authorizationFactory = authorizationFactory;
    this.accountManager = accountManager;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.payer = payer;
  }

  public Completable createWebPaymentAuthorization(int paymentId) {
    return CreatePaymentAuthorizationRequest.of(paymentId, bodyInterceptorV3, httpClient,
        converterFactory)
        .observe(true)
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Observable.just(null);
          }
          return Observable.<Void>error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        })
        .toCompletable()
        .andThen(syncAuthorization(paymentId, AuthorizationFactory.WEB));
  }

  public Completable createPayPalPaymentAuthorization(int paymentId, String authorizationCode) {
    return CreatePaymentAuthorizationRequest.of(paymentId, authorizationCode, bodyInterceptorV3,
        httpClient, converterFactory)
        .observe(true)
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Observable.just(null);
          }
          return Observable.<Void>error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        })
        .toCompletable()
        .andThen(syncAuthorization(paymentId, AuthorizationFactory.PAYPAL));
  }

  public Completable saveAuthorization(Authorization authorization) {
    return Completable.fromAction(() -> authotizationAccessor.insert(
        authorizationFactory.convertToDatabasePaymentAuthorization(authorization)));
  }

  public Observable<Authorization> getPaymentAuthorization(int paymentId,
      String authorizationType) {
    return payer.getId()
        .flatMapObservable(
            payerId -> authotizationAccessor.getPaymentAuthorization(payerId, paymentId))
        .flatMap(authorizations -> Observable.from(authorizations)
            .map(paymentAuthorization -> authorizationFactory.convertToPaymentAuthorization(
                paymentAuthorization))
            .switchIfEmpty(syncAuthorization(paymentId, authorizationType).toObservable()));
  }

  private Completable syncAuthorization(int paymentId, String authorizationType) {
    return backgroundSync.scheduleAuthorizationSync(paymentId, authorizationType);
  }
}