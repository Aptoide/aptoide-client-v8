package cm.aptoide.pt.networking;

import android.content.SharedPreferences;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.utils.q.QManager;
import rx.Single;
import rx.schedulers.Schedulers;

public class BodyInterceptorV3 implements BodyInterceptor<BaseBody> {

  public static final String RESPONSE_MODE_JSON = "json";

  private final String aptoideMd5sum;
  private final String aptoidePackage;
  private final IdsRepository idsRepository;
  private final AuthenticationPersistence authenticationPersistence;
  private final QManager qManager;
  private final SharedPreferences sharedPreferences;
  private final String responseMode;
  private final int androidVersion;
  private final NetworkOperatorManager operatorManager;

  public BodyInterceptorV3(IdsRepository idsRepository, String aptoideMd5sum, String aptoidePackage,
      QManager qManager, SharedPreferences sharedPreferences, String responseMode,
      int androidVersion, NetworkOperatorManager operatorManager,
      AuthenticationPersistence authenticationPersistence) {
    this.idsRepository = idsRepository;
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.authenticationPersistence = authenticationPersistence;
    this.qManager = qManager;
    this.sharedPreferences = sharedPreferences;
    this.responseMode = responseMode;
    this.androidVersion = androidVersion;
    this.operatorManager = operatorManager;
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return authenticationPersistence.getAuthentication()
        .map(authentication -> {
          body.setAndroidVersion(androidVersion);
          body.setAptoideMd5sum(aptoideMd5sum);
          body.setAptoidePackage(aptoidePackage);
          body.setAptoideUid(idsRepository.getUniqueIdentifier());
          body.setQ(qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)));
          body.setResponseMode(responseMode);

          if (authentication.isAuthenticated()) {
            body.setAccessToken(authentication.getAccessToken());
          }

          final String forceCountry = ToolboxManager.getForceCountry(sharedPreferences);

          if (!TextUtils.isEmpty(forceCountry)) {
            body.setSimCountryISOCode(forceCountry);
          } else {
            if (operatorManager.isSimStateReady()) {
              body.setMobileCountryCode(operatorManager.getMobileCountryCode());
              body.setMobileNetworkCode(operatorManager.getMobileNetworkCode());
              body.setSimCountryISOCode(operatorManager.getSimCountryISO());
            }
          }

          return body;
        })
        .subscribeOn(Schedulers.computation());
  }
}
