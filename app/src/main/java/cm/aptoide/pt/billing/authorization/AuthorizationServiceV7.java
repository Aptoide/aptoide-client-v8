package cm.aptoide.pt.billing.authorization;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.UpdateAuthorizationRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class AuthorizationServiceV7 implements AuthorizationService {

  private final AuthorizationMapper authorizationMapper;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final BodyInterceptor<BaseBody> bodyInterceptorV7;

  public AuthorizationServiceV7(AuthorizationMapper authorizationMapper, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, BodyInterceptor<BaseBody> bodyInterceptorV7) {
    this.authorizationMapper = authorizationMapper;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.bodyInterceptorV7 = bodyInterceptorV7;
  }

  @Override public Single<Authorization> updateAuthorization(long transactionId, String metadata) {
    return UpdateAuthorizationRequest.of(transactionId, metadata, sharedPreferences, httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(authorizationMapper.map(response.getData()));
          }
          return Single.error(new IllegalArgumentException(V7.getErrorMessage(response)));
        });
  }

  @Override public Single<Authorization> getAuthorization(long transactionId) {
    return GetAuthorizationRequest.of(transactionId, sharedPreferences, httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(authorizationMapper.map(response.getData()));
          }
          return Single.error(new IllegalArgumentException(V7.getErrorMessage(response)));
        });
  }
}