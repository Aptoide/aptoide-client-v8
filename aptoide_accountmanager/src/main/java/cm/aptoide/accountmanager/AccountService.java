package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.OAuth2AuthenticationRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import rx.Single;

public class AccountService {

  private final AptoideClientUUID aptoideClientUUID;
  private final BodyInterceptor<BaseBody> baseBodyInterceptorV3;

  public AccountService(AptoideClientUUID aptoideClientUUID,
      BodyInterceptor<BaseBody> baseBodyInterceptorV3) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.baseBodyInterceptorV3 = baseBodyInterceptorV3;
  }

  public Single<String> refreshToken(String refreshToken) {
    return OAuth2AuthenticationRequest.of(refreshToken, aptoideClientUUID.getUniqueIdentifier(),
        baseBodyInterceptorV3).observe().toSingle().flatMap(oAuth -> {
      if (!oAuth.hasErrors()) {
        return Single.just(oAuth.getAccessToken());
      } else {
        return Single.error(new AccountException(oAuth.getError()));
      }
    });
  }
}