package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.OAuth2AuthenticationRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class AccountService {

  private final AptoideClientUUID aptoideClientUUID;
  private final BodyInterceptor<BaseBody> baseBodyInterceptorV3;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;

  public AccountService(AptoideClientUUID aptoideClientUUID,
      BodyInterceptor<BaseBody> baseBodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.baseBodyInterceptorV3 = baseBodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
  }

  public Single<String> refreshToken(String refreshToken) {
    return OAuth2AuthenticationRequest.of(refreshToken, aptoideClientUUID.getUniqueIdentifier(),
        baseBodyInterceptorV3, httpClient, converterFactory).observe().toSingle().flatMap(oAuth -> {
      if (!oAuth.hasErrors()) {
        return Single.just(oAuth.getAccessToken());
      } else {
        return Single.error(new AccountException(oAuth.getError()));
      }
    });
  }
}