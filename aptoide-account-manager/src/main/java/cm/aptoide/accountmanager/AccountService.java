package cm.aptoide.accountmanager;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.OAuth2AuthenticationRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class AccountService {

  private final BodyInterceptor<BaseBody> baseBodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public AccountService(BodyInterceptor<BaseBody> baseBodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.baseBodyInterceptorV3 = baseBodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Single<String> refreshToken(String refreshToken) {
    return OAuth2AuthenticationRequest.of(refreshToken, baseBodyInterceptorV3, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .flatMap(oAuth -> {
          if (!oAuth.hasErrors()) {
            return Single.just(oAuth.getAccessToken());
          } else {
            return Single.error(new AccountException(oAuth.getError()));
          }
        });
  }
}
