package cm.aptoide.pt.v8engine.networking;

import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.v8engine.V8Engine;
import rx.Single;
import rx.schedulers.Schedulers;

public class BaseBodyInterceptorV3 extends BaseBodyInterceptor<BaseBody> {

  public BaseBodyInterceptorV3(String aptoideMd5sum, String aptoidePackage,
      IdsRepository idsRepository, QManager qManager) {
    super(aptoideMd5sum, aptoidePackage, idsRepository, qManager);
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return Single.just(body)
        .doOnEach(notification -> {
          body.setAptoideMd5sum(aptoideMd5sum);
          body.setAptoidePackage(aptoidePackage);
          body.setAptoideUID(idsRepository.getUniqueIdentifier());
          body.setQ(V8Engine.getQManager()
              .getFilters(ManagerPreferences.getHWSpecsFilter()));
        })
        .subscribeOn(Schedulers.computation());
  }
}
