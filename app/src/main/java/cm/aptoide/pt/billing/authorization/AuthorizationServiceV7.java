package cm.aptoide.pt.billing.authorization;

import android.content.SharedPreferences;
import cm.aptoide.pt.billing.IdResolver;
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

  private final AuthorizationMapperV7 authorizationMapper;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final BodyInterceptor<BaseBody> bodyInterceptorV7;
  private final IdResolver idResolver;

  public AuthorizationServiceV7(AuthorizationMapperV7 authorizationMapper, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, BodyInterceptor<BaseBody> bodyInterceptorV7,
      IdResolver idResolver) {
    this.authorizationMapper = authorizationMapper;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.idResolver = idResolver;
  }

  @Override
  public Single<Authorization> updateAuthorization(String transactionId, String metadata) {
    return UpdateAuthorizationRequest.of(idResolver.resolveTransactionId(transactionId),
        metadata, sharedPreferences, httpClient, converterFactory, bodyInterceptorV7,
        tokenInvalidator)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(authorizationMapper.map(response.getData(), transactionId));
          }
          return Single.error(new IllegalArgumentException(V7.getErrorMessage(response)));
        });
  }

  @Override public Single<Authorization> getAuthorization(String transactionId) {
    return GetAuthorizationRequest.of(idResolver.resolveTransactionId(transactionId),
        sharedPreferences, httpClient, converterFactory, bodyInterceptorV7, tokenInvalidator)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(authorizationMapper.map(response.getData(), transactionId));
          }
          return Single.error(new IllegalArgumentException(V7.getErrorMessage(response)));
        });
  }
}