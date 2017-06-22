package cm.aptoide.pt.dataprovider.ws;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;

/**
 * Created by diogoloureiro on 10/08/16.
 */
public class BaseBodyDecorator {

  private final String aptoideClientUUID;

  public BaseBodyDecorator(String aptoideClientUUID) {
    this.aptoideClientUUID = aptoideClientUUID;
  }

  public BaseBody decorate(BaseBody baseBody, String accessToken) {

    String access_token = accessToken;
    if (!TextUtils.isEmpty(access_token)) {
      baseBody.setAccessToken(access_token);
    }

    baseBody.setAptoideId(aptoideClientUUID);
    baseBody.setAptoideVercode(AptoideUtils.Core.getVerCode());
    baseBody.setCdn("pool");
    baseBody.setLang(ManagerPreferences.getLanguage());
    baseBody.setMature(Api.isMature());
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
