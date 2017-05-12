/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaymentAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.v8engine.payment.Authorization;
import cm.aptoide.pt.v8engine.payment.repository.sync.PaymentSyncScheduler;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;

public class PaymentAuthorizationRepository {

  private final PaymentAuthorizationAccessor authotizationAccessor;
  private final PaymentSyncScheduler backgroundSync;
  private final PaymentAuthorizationFactory authorizationFactory;
  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;

  public PaymentAuthorizationRepository(PaymentAuthorizationAccessor authorizationAccessor,
      PaymentSyncScheduler backgroundSync, PaymentAuthorizationFactory authorizationFactory,
      AptoideAccountManager accountManager, BodyInterceptor<BaseBody> bodyInterceptorV3,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    this.authotizationAccessor = authorizationAccessor;
    this.backgroundSync = backgroundSync;
    this.authorizationFactory = authorizationFactory;
    this.accountManager = accountManager;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
  }

  public Completable createPaymentAuthorization(int paymentId) {
    return CreatePaymentAuthorizationRequest.of(accountManager.getAccessToken(), paymentId,
        bodyInterceptorV3, httpClient, converterFactory)
        .observe()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Observable.just(null);
          }
          return Observable.<Void>error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        })
        .toCompletable();
  }

  public Observable<Authorization> getPaymentAuthorization(int paymentId, String payerId) {
    return getPaymentAuthorizations(Collections.singletonList(paymentId), payerId).flatMapIterable(
        authorizations -> authorizations)
        .filter(authorization -> authorization.getPaymentId() == paymentId);
  }

  public Observable<List<Authorization>> getPaymentAuthorizations(List<Integer> paymentIds,
      String payerId) {
    return syncAuthorizations(paymentIds).andThen(
        authotizationAccessor.getPaymentAuthorizations(payerId)
            .flatMap(paymentAuthorizations -> Observable.from(paymentAuthorizations)
                .map(paymentAuthorization -> authorizationFactory.convertToPaymentAuthorization(
                    paymentAuthorization))
                .toList()));
  }

  private Completable syncAuthorizations(List<Integer> paymentIds) {
    return Observable.from(paymentIds)
        .map(paymentId -> String.valueOf(paymentId))
        .toList()
        .flatMap(ids -> backgroundSync.syncAuthorizations(ids)
            .toObservable())
        .toCompletable();
  }

  public Completable saveAuthorization(Authorization authorization) {
    return Completable.fromAction(() -> authotizationAccessor.save(
        authorizationFactory.convertToDatabasePaymentAuthorization(authorization)))
        .andThen(syncAuthorizations(Collections.singletonList(authorization.getPaymentId())));
  }
}
