package cm.aptoide.pt.v8engine.networking;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.v8engine.PackageRepository;
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
  private final SharedPreferences sharedPreferences;
  private final Resources resources;
  private final String packageName;
  private final PackageRepository packageRepository;

  public BaseBodyInterceptorV7(IdsRepository idsRepository, AptoideAccountManager accountManager,
      AdultContent adultContent, String aptoideMd5sum, String aptoidePackage, QManager qManager,
      String cdn, SharedPreferences sharedPreferences, Resources resources, String packageName,
      PackageRepository packageRepository) {
    this.idsRepository = idsRepository;
    this.accountManager = accountManager;
    this.adultContent = adultContent;
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.qManager = qManager;
    this.cdn = cdn;
    this.packageRepository = packageRepository;
    this.adultContentDefaultValue = null;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
    this.packageName = packageName;
  }

  public BaseBodyInterceptorV7(String aptoideMd5sum, String aptoidePackage,
      IdsRepository idsRepository, AptoideAccountManager accountManager, AdultContent adultContent,
      QManager qManager, String cdn, boolean mature, SharedPreferences sharedPreferences,
      Resources resources, String packageName, PackageRepository packageRepository) {
    this.cdn = cdn;
    this.accountManager = accountManager;
    this.adultContent = adultContent;
    this.adultContentDefaultValue = mature;
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.idsRepository = idsRepository;
    this.qManager = qManager;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
    this.packageName = packageName;
    this.packageRepository = packageRepository;
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return Single.zip(adultContent.enabled()
            .first()
            .toSingle(), accountManager.accountStatus()
            .first()
            .toSingle(), packageRepository.getPackageVersionCode(packageName),
        (adultContentEnabled, account, aptoideVersionCode) -> {
          if (account.isLoggedIn()) {
            body.setAccessToken(account.getAccessToken());
          }

          body.setAptoideId(idsRepository.getUniqueIdentifier());
          body.setAptoideVercode(aptoideVersionCode);
          body.setCdn(cdn);
          body.setLang(AptoideUtils.SystemU.getCountryCode(resources));
          if (adultContentDefaultValue == null) {
            body.setMature(adultContentEnabled);
          } else {
            body.setMature(adultContentDefaultValue);
          }
          body.setQ(qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)));
          String forceCountry = ToolboxManager.getForceCountry(sharedPreferences);
          if (!TextUtils.isEmpty(forceCountry)) {
            body.setCountry(forceCountry);
          }
          body.setAptoideMd5sum(aptoideMd5sum);
          body.setAptoidePackage(aptoidePackage);

          return body;
        })
        .subscribeOn(Schedulers.computation());
  }
}
