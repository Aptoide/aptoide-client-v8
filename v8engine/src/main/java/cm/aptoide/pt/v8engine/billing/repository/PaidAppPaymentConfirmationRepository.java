/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.BaseV3Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaymentConfirmationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.billing.repository.sync.PaymentSyncScheduler;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class PaidAppPaymentConfirmationRepository extends PaymentConfirmationRepository {

  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final Converter.Factory converterFactory;
  private final OkHttpClient httpClient;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public PaidAppPaymentConfirmationRepository(NetworkOperatorManager operatorManager,
      PaymentConfirmationAccessor confirmationAccessor, PaymentSyncScheduler backgroundSync,
      PaymentConfirmationFactory confirmationFactory, AptoideAccountManager accountManager,
      BodyInterceptor<BaseBody> bodyInterceptorV3, Converter.Factory converterFactory,
      OkHttpClient httpClient, Payer payer, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(operatorManager, confirmationAccessor, backgroundSync, confirmationFactory, payer);
    this.accountManager = accountManager;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public Completable createPaymentConfirmation(int paymentId, Product product) {
    return CreatePaymentConfirmationRequest.ofPaidApp(product.getId(), paymentId, operatorManager,
        ((PaidAppProduct) product).getStoreName(), bodyInterceptorV3, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, ((PaidAppProduct) product).getPackageVersionCode())
        .observe(true)
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
  protected Single<BaseV3Response> createServerConfirmation(Product product, int paymentId,
      String metadataId) {
    return CreatePaymentConfirmationRequest.ofPaidApp(product.getId(), paymentId, operatorManager,
        ((PaidAppProduct) product).getStoreName(), metadataId, bodyInterceptorV3, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences,
        ((PaidAppProduct) product).getPackageVersionCode())
        .observe(true)
        .toSingle();
  }
}
