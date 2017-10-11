package cm.aptoide.pt.billing.authorization;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.pt.billing.Customer;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreateTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetTransactionRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class AuthorizationServiceV3 implements AuthorizationService {

  private final AuthorizationFactory authorizationFactory;
  private final AuthorizationMapper authorizationMapper;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final Customer customer;
  private final Resources resources;

  public AuthorizationServiceV3(AuthorizationFactory authorizationFactory,
      AuthorizationMapper authorizationMapper, BodyInterceptor<BaseBody> bodyInterceptorV3,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, Customer customer,
      Resources resources) {
    this.authorizationFactory = authorizationFactory;
    this.authorizationMapper = authorizationMapper;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.customer = customer;
    this.resources = resources;
  }

  @Override public Single<Authorization> getAuthorization(long transactionId) {
    return customer.getId()
        .flatMap(customerId -> Single.zip(
            GetApkInfoRequest.of(transactionId, bodyInterceptorV3, httpClient, converterFactory,
                tokenInvalidator, sharedPreferences, resources)
                .observe(false)
                .toSingle(),
            GetTransactionRequest.of(transactionId, bodyInterceptorV3, httpClient, converterFactory,
                tokenInvalidator, sharedPreferences)
                .observe(true)
                .toSingle(),
            (paidApp, transactionResponse) -> authorizationMapper.map(customerId, transactionId,
                AuthorizationFactory.PAYPAL_SDK, transactionResponse, paidApp)));
  }

  @Override public Single<Authorization> updateAuthorization(long transactionId, String metadata) {
    return Single.zip(
        GetApkInfoRequest.of(transactionId, bodyInterceptorV3, httpClient, converterFactory,
            tokenInvalidator, sharedPreferences, resources)
            .observe(false)
            .toSingle(), customer.getId(), (paidApp, customerId) -> {

          if (paidApp.isOk()) {
            return CreateTransactionRequest.of(paidApp.getPayment()
                    .getMetadata()
                    .getProductId(), 1, paidApp.getPath()
                    .getStoreName(), metadata, bodyInterceptorV3, httpClient, converterFactory,
                tokenInvalidator, sharedPreferences, 3, paidApp.getApp()
                    .getName())
                .observe(true)
                .toSingle()
                .map(response -> authorizationMapper.map(customerId, 1,
                    AuthorizationFactory.PAYPAL_SDK, response, paidApp));
          }
          return Single.just(
              authorizationMapper.map(customerId, 1, transactionId, AuthorizationFactory.PAYPAL_SDK,
                  Authorization.Status.FAILED, paidApp));
        })
        .flatMap(single -> single);
  }
}