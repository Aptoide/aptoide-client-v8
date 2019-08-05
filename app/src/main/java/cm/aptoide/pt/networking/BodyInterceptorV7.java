package cm.aptoide.pt.networking;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.preferences.AptoideMd5Manager;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.q.QManager;
import rx.Single;
import rx.schedulers.Schedulers;

public class BodyInterceptorV7 implements BodyInterceptor<BaseBody> {

  private final IdsRepository idsRepository;
  private final AuthenticationPersistence authenticationPersistence;
  private final AptoideMd5Manager aptoideMd5Manager;
  private final String aptoidePackage;
  private final QManager qManager;
  private final Cdn cdn;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;
  private final int aptoideVersionCode;

  public BodyInterceptorV7(IdsRepository idsRepository,
      AuthenticationPersistence authenticationPersistence, AptoideMd5Manager aptoideMd5Manager,
      String aptoidePackage, QManager qManager, Cdn cdn, SharedPreferences sharedPreferences,
      Resources resources, int aptoideVersionCode) {
    this.idsRepository = idsRepository;
    this.authenticationPersistence = authenticationPersistence;
    this.aptoideMd5Manager = aptoideMd5Manager;
    this.aptoidePackage = aptoidePackage;
    this.qManager = qManager;
    this.cdn = cdn;
    this.aptoideVersionCode = aptoideVersionCode;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return authenticationPersistence.getAuthentication()
        .map(authentication -> {

          if (authentication.isAuthenticated()) {
            body.setAccessToken(authentication.getAccessToken());
          } else {
            body.setAccessToken(null);
          }

          body.setAptoideId(idsRepository.getUniqueIdentifier());
          body.setAptoideVercode(aptoideVersionCode);
          body.setCdn(cdn.name()
              .toLowerCase());
          body.setLang(AptoideUtils.SystemU.getCountryCode(resources));

          body.setQ(qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)));
          String forceCountry = ToolboxManager.getForceCountry(sharedPreferences);
          if (!TextUtils.isEmpty(forceCountry)) {
            body.setCountry(forceCountry);
          }
          body.setAptoideMd5sum(aptoideMd5Manager.getAptoideMd5());
          body.setAptoidePackage(aptoidePackage);

          return body;
        })
        .subscribeOn(Schedulers.computation());
  }
}
