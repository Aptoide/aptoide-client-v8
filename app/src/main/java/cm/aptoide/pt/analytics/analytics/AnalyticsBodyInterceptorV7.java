package cm.aptoide.pt.analytics.analytics;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.analytics.implementation.network.AnalyticsBaseBody;
import cm.aptoide.analytics.implementation.network.AnalyticsBodyInterceptor;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.q.QManager;
import rx.Single;
import rx.schedulers.Schedulers;

public class AnalyticsBodyInterceptorV7 implements AnalyticsBodyInterceptor<AnalyticsBaseBody> {
  private final IdsRepository idsRepository;
  private final AuthenticationPersistence authenticationPersistence;
  private final String aptoideMd5sum;
  private final String aptoidePackage;
  private final Resources resources;
  private final int aptoideVersionCode;
  private final QManager qManager;
  private final SharedPreferences sharedPreferences;

  public AnalyticsBodyInterceptorV7(IdsRepository idsRepository,
      AuthenticationPersistence authenticationPersistence, String aptoideMd5sum,
      String aptoidePackage, Resources resources, int aptoideVersionCode, QManager qManager,
      SharedPreferences sharedPreferences) {
    this.idsRepository = idsRepository;
    this.authenticationPersistence = authenticationPersistence;
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.resources = resources;
    this.aptoideVersionCode = aptoideVersionCode;
    this.qManager = qManager;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public Single<AnalyticsBaseBody> intercept(AnalyticsBaseBody body) {
    return authenticationPersistence.getAuthentication()
        .map(authentication -> {
          if (authentication.isAuthenticated()) {
            body.setAccessToken(authentication.getAccessToken());
          }

          body.setAptoideId(idsRepository.getUniqueIdentifier());
          body.setAptoideVercode(aptoideVersionCode);
          body.setLang(AptoideUtils.SystemU.getCountryCode(resources));
          body.setQ(qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)));
          body.setAptoideMd5sum(aptoideMd5sum);
          body.setAptoidePackage(aptoidePackage);

          return body;
        })
        .subscribeOn(Schedulers.computation());
  }
}
