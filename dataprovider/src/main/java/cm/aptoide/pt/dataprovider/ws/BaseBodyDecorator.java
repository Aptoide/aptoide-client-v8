package cm.aptoide.pt.dataprovider.ws;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.AllArgsConstructor;

/**
 * Created by diogoloureiro on 10/08/16.
 */
@AllArgsConstructor public class BaseBodyDecorator {

  private final IdsRepositoryImpl idsRepository;

  public BaseBody decorate(BaseBody baseBody, String accessToken) {

    String access_token = accessToken;
    if (!TextUtils.isEmpty(access_token)) {
      baseBody.setAccessToken(access_token);
    }

    baseBody.setAptoideId(idsRepository.getAptoideClientUUID());
    baseBody.setAptoideVercode(AptoideUtils.Core.getVerCode());
    baseBody.setCdn("pool");
    baseBody.setLang(Api.LANG);
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
