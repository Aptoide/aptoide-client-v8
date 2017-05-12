/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaymentConfirmationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.repository.sync.PaymentSyncScheduler;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;

/**
 * Created by marcelobenites on 8/18/16.
 */
public class InAppPaymentConfirmationRepository extends PaymentConfirmationRepository {

  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;

  public InAppPaymentConfirmationRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor paymentDatabase, PaymentSyncScheduler backgroundSync,
      PaymentConfirmationFactory confirmationFactory, AptoideAccountManager accountManager,
      BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory, Payer payer) {
    super(operatorManager, paymentDatabase, backgroundSync, confirmationFactory, payer);
    this.accountManager = accountManager;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
  }

  @Override public Completable createPaymentConfirmation(int paymentId, Product product) {

    return CreatePaymentConfirmationRequest.ofInApp(product.getId(), paymentId, operatorManager,
        ((InAppProduct) product).getDeveloperPayload(), bodyInterceptorV3, httpClient,
        converterFactory)
        .observe()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Observable.just(null);
          }
          return Observable.error(
              new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        })
        .toCompletable()
        .andThen(syncPaymentConfirmation(product));
  }

  @Override
  public Completable createPaymentConfirmation(int paymentId, String paymentConfirmationId,
      Product product) {
    return createPaymentConfirmation(product, paymentId, paymentConfirmationId);
  }
}