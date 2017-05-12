package cm.aptoide.pt.v8engine.networking;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import rx.Single;
import rx.schedulers.Schedulers;

public class BaseBodyInterceptorV3 implements BodyInterceptor<BaseBody> {

  private final String aptoideMd5sum;
  private final String aptoidePackage;
  private final IdsRepository idsRepository;
  private final AptoideAccountManager accountManager;

  public BaseBodyInterceptorV3(IdsRepository idsRepository, String aptoideMd5sum,
      String aptoidePackage, AptoideAccountManager accountManager) {
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.idsRepository = idsRepository;
    this.accountManager = accountManager;
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .map(account -> {
          body.setAptoideMd5sum(aptoideMd5sum);
          body.setAptoidePackage(aptoidePackage);
          body.setAptoideUid(idsRepository.getUniqueIdentifier());
          if (account.isLoggedIn()) {
            body.setAccessToken(account.getAccessToken());
          }
          return body;
        })
        .subscribeOn(Schedulers.computation());
  }
}
