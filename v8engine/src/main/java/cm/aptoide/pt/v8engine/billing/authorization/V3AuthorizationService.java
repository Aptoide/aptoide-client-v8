package cm.aptoide.pt.v8engine.billing.authorization;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaymentAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetPaymentAuthorizationsRequest;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class V3AuthorizationService implements AuthorizationService {

  private final AuthorizationFactory authorizationFactory;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public V3AuthorizationService(AuthorizationFactory authorizationFactory,
      BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.authorizationFactory = authorizationFactory;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public Single<Authorization> createAuthorization(String payerId, int paymentId) {
    return CreatePaymentAuthorizationRequest.of(paymentId, bodyInterceptorV3, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMap(response -> Single.just(authorizationFactory.map(response, payerId, paymentId)));
  }

  @Override public Single<List<Authorization>> getAuthorizations(String payerId, int paymentId) {
    return GetPaymentAuthorizationsRequest.of(bodyInterceptorV3, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .map(response -> authorizationFactory.map(response, payerId, paymentId));
  }
}