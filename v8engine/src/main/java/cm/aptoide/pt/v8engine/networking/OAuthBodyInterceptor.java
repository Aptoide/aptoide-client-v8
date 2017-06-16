package cm.aptoide.pt.v8engine.networking;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.utils.q.QManager;
import rx.Single;

public class OAuthBodyInterceptor extends BaseBodyInterceptorV3 {

  private final AptoideAccountManager accountManager;

  public OAuthBodyInterceptor(IdsRepository idsRepository, String aptoideMd5sum,
      String aptoidePackage, AptoideAccountManager accountManager, QManager qManager,
      SharedPreferences sharedPreferences) {
    super(idsRepository, aptoideMd5sum, aptoidePackage, accountManager, qManager,
        sharedPreferences);
    this.accountManager = accountManager;
  }

  @Override public Single<BaseBody> intercept(BaseBody body) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> super.intercept(body)
            .map(baseBody -> {
              if (account.isLoggedIn()) {
                baseBody.put("oauthToken", account.getAccessToken());
              }
              return baseBody;
            }));
  }
}
