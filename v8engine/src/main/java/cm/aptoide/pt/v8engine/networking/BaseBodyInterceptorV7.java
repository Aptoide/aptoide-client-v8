package cm.aptoide.pt.v8engine.networking;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import rx.Single;
import rx.schedulers.Schedulers;

public class BaseBodyInterceptorV7 implements BodyInterceptor<BaseBody> {

  private final IdsRepository idsRepository;
  private final AptoideAccountManager accountManager;
  private final AdultContent adultContent;
  private final String aptoideMd5sum;
  private final String aptoidePackage;

  public BaseBodyInterceptorV7(IdsRepository idsRepository, AptoideAccountManager accountManager,
      AdultContent adultContent, String aptoideMd5sum, String aptoidePackage) {
    this.idsRepository = idsRepository;
    this.accountManager = accountManager;
    this.adultContent = adultContent;
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return Single.zip(adultContent.enabled()
        .first()
        .toSingle(), accountManager.accountStatus()
        .first()
        .toSingle(), (adultContentEnabled, account) -> {
      if (account.isLoggedIn()) {
        body.setAccessToken(account.getAccessToken());
      }

      body.setAptoideId(idsRepository.getUniqueIdentifier());
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
      body.setAptoideMd5sum(aptoideMd5sum);
      body.setAptoidePackage(aptoidePackage);

      return body;
    })
        .subscribeOn(Schedulers.computation());
  }
}
