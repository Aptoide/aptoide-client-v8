package cm.aptoide.pt.v8engine.networking;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import rx.Single;
import rx.schedulers.Schedulers;

public class BaseBodyInterceptorV7 implements BodyInterceptor<BaseBody> {

  private final IdsRepository idsRepository;
  private final AptoideAccountManager accountManager;
  private final AdultContent adultContent;
  private final String aptoideMd5sum;
  private final String aptoidePackage;
  private final QManager qManager;
  private final String cdn;
  private final Boolean adultContentDefaultValue;

  public BaseBodyInterceptorV7(IdsRepository idsRepository, AptoideAccountManager accountManager,
      AdultContent adultContent, String aptoideMd5sum, String aptoidePackage, QManager qManager,
      String cdn) {
    this.idsRepository = idsRepository;
    this.accountManager = accountManager;
    this.adultContent = adultContent;
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.qManager = qManager;
    this.cdn = cdn;
    this.adultContentDefaultValue = null;
  }

  public BaseBodyInterceptorV7(String aptoideMd5sum, String aptoidePackage,
      IdsRepository idsRepository, AptoideAccountManager accountManager, AdultContent adultContent,
      QManager qManager, String cdn, boolean mature) {
    this.cdn = cdn;
    this.accountManager = accountManager;
    this.adultContent = adultContent;
    this.adultContentDefaultValue = mature;
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.idsRepository = idsRepository;
    this.qManager = qManager;
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
      body.setCdn(cdn);
      body.setLang(AptoideUtils.SystemU.getCountryCode());
      if (adultContentDefaultValue == null) {
        body.setMature(adultContentEnabled);
      } else {
        body.setMature(adultContentDefaultValue);
      }
      body.setQ(qManager
          .getFilters(ManagerPreferences.getHWSpecsFilter()));
      if (ToolboxManager.isDebug()) {
        String forceCountry = ToolboxManager.getForceCountry();
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
