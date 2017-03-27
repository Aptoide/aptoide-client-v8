package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.ws.v3.OAuth2AuthenticationRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import rx.Single;

public class AccountService {

  private final AptoideClientUUID aptoideClientUUID;

  public AccountService(AptoideClientUUID aptoideClientUUID) {
    this.aptoideClientUUID = aptoideClientUUID;
  }

  public Single<String> refreshToken(String refreshToken) {
    return OAuth2AuthenticationRequest.of(refreshToken, aptoideClientUUID.getUniqueIdentifier())
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