package cm.aptoide.pt.networking;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.AccountException;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.OAuth2AuthenticationRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;

public class RefreshTokenInvalidator implements TokenInvalidator {

  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final SharedPreferences sharedPreferences;
  private final String extraId;
  private final TokenInvalidator tokenInvalidator;
  private final AuthenticationPersistence authenticationPersistence;

  public RefreshTokenInvalidator(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, SharedPreferences sharedPreferences, String extraId,
      TokenInvalidator tokenInvalidator, AuthenticationPersistence authenticationPersistence) {
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.sharedPreferences = sharedPreferences;
    this.extraId = extraId;
    this.tokenInvalidator = tokenInvalidator;
    this.authenticationPersistence = authenticationPersistence;
  }

  @Override public Completable invalidateAccessToken() {
    return authenticationPersistence.getAuthentication()
        .flatMapCompletable(
            authentication -> OAuth2AuthenticationRequest.of(authentication.getRefreshToken(),
                bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences,
                extraId)
                .observe()
                .toSingle()
                .flatMapCompletable(oAuth -> {
                  if (!oAuth.hasErrors()) {
                    return authenticationPersistence.updateAuthentication(oAuth.getAccessToken());
                  } else {
                    return Completable.error(new AccountException(oAuth.getError()));
                  }
                }));
  }
}
