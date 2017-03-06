package cm.aptoide.pt.v8engine;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyDecorator;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;

/**
 * Created by diogoloureiro on 10/08/16.
 */
public class BaseBodyDecorator implements BodyDecorator {

  private final String aptoideClientUUID;
  private final AptoideAccountManager accountManager;

  public BaseBodyDecorator(String aptoideClientUUID, AptoideAccountManager accountManager) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.accountManager = accountManager;
  }

  public BaseBody decorate(BaseBody baseBody) {

    if (!TextUtils.isEmpty(accountManager.getAccessToken())) {
      baseBody.setAccessToken(accountManager.getAccessToken());
    }

    baseBody.setAptoideId(aptoideClientUUID);
    baseBody.setAptoideVercode(AptoideUtils.Core.getVerCode());
    baseBody.setCdn("pool");
    baseBody.setLang(Api.LANG);
    baseBody.setMature(accountManager.isAccountMature());
    if (ManagerPreferences.getHWSpecsFilter()) {
      baseBody.setQ(Api.Q);
    }
    if (ManagerPreferences.isDebug()) {
      String forceCountry = ManagerPreferences.getForceCountry();
      if (!TextUtils.isEmpty(forceCountry)) {
        baseBody.setCountry(forceCountry);
      }
    }

    return baseBody;
  }
}
