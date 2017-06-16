package cm.aptoide.pt.v8engine.networking;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.q.QManager;
import rx.Single;
import rx.schedulers.Schedulers;

public class BaseBodyInterceptorV3 implements BodyInterceptor<BaseBody> {

  private final String aptoideMd5sum;
  private final String aptoidePackage;
  private final IdsRepository idsRepository;
  private final AptoideAccountManager accountManager;
  private final QManager qManager;
  private SharedPreferences sharedPreferences;

  public BaseBodyInterceptorV3(IdsRepository idsRepository, String aptoideMd5sum,
      String aptoidePackage, AptoideAccountManager accountManager, QManager qManager,
      SharedPreferences sharedPreferences) {
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.idsRepository = idsRepository;
    this.accountManager = accountManager;
    this.qManager = qManager;
    this.sharedPreferences = sharedPreferences;
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .map(account -> {
          body.setAptoideMd5sum(aptoideMd5sum);
          body.setAptoidePackage(aptoidePackage);
          body.setAptoideUid(idsRepository.getUniqueIdentifier());
          body.setQ(qManager
              .getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)));
          if (account.isLoggedIn()) {
            body.setAccessToken(account.getAccessToken());
          }
          return body;
        })
        .subscribeOn(Schedulers.computation());
  }
}
