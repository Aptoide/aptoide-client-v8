package cm.aptoide.pt.v8engine;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import rx.Single;
import rx.schedulers.Schedulers;

public class BaseBodyInterceptor implements BodyInterceptor<BaseBody> {

  private final AptoideClientUUID aptoideClientUUID;
  private final AptoideAccountManager accountManager;
  private final AdultContent adultContent;

  public BaseBodyInterceptor(AptoideClientUUID aptoideClientUUID,
      AptoideAccountManager accountManager, AdultContent adultContent) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.accountManager = accountManager;
    this.adultContent = adultContent;
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return Single.zip(adultContent.enabled().first().toSingle(),
        accountManager.accountStatus().first().toSingle(), (adultContentEnabled, account) -> {
          if (account.isLoggedIn()) {
            body.setAccessToken(account.getAccessToken());
          }

          body.setAptoideId(aptoideClientUUID.getUniqueIdentifier());
          body.setAptoideVercode(AptoideUtils.Core.getVerCode());
          body.setCdn("pool");
          body.setLang(Api.LANG);
          body.setMature(adultContentEnabled);
          if (ManagerPreferences.getHWSpecsFilter()) {
            body.setQ(Api.Q);
          }
          if (ManagerPreferences.isDebug()) {
            String forceCountry = ManagerPreferences.getForceCountry();
            if (!TextUtils.isEmpty(forceCountry)) {
              body.setCountry(forceCountry);
            }
          }

          return body;
        }).subscribeOn(Schedulers.computation());
  }
}
