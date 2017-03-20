package cm.aptoide.pt.v8engine;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Created by diogoloureiro on 10/08/16.
 */
public class BaseBodyInterceptor implements BodyInterceptor<BaseBody> {

  private final AptoideClientUUID aptoideClientUUID;
  private final AptoideAccountManager accountManager;

  public BaseBodyInterceptor(AptoideClientUUID aptoideClientUUID,
      AptoideAccountManager accountManager) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.accountManager = accountManager;
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return addBasicData(body).flatMap(
        bodyWithoutAccountData -> addAccountData(bodyWithoutAccountData))
        .subscribeOn(Schedulers.computation());
  }

  private Single<BaseBody> addBasicData(BaseBody body) {
    return Single.<BaseBody> fromCallable(() -> {
      body.setAptoideId(aptoideClientUUID.getUniqueIdentifier());
      body.setAptoideVercode(AptoideUtils.Core.getVerCode());
      body.setCdn("pool");
      body.setLang(Api.LANG);

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
    });
  }

  private Single<BaseBody> addAccountData(BaseBody body) {
    return accountManager.getAccountAsync().map(account -> {
      if (!TextUtils.isEmpty(accountManager.getAccessToken())) {
        body.setAccessToken(accountManager.getAccessToken());
      }
      body.setMature(accountManager.isAccountMature());
      return body;
    }).onErrorReturn(throwable -> body);
  }
}
