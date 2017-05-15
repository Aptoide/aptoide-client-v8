package cm.aptoide.pt.v8engine.networking;

import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import rx.Single;
import rx.schedulers.Schedulers;

public class BaseBodyInterceptorV3 extends BaseBodyInterceptor<BaseBody> {

  public BaseBodyInterceptorV3(String aptoideMd5sum, String aptoidePackage,
      IdsRepository idsRepository) {
    super(aptoideMd5sum, aptoidePackage, idsRepository);
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return Single.just(body)
        .doOnEach(notification -> {
          body.setAptoideMd5sum(aptoideMd5sum);
          body.setAptoidePackage(aptoidePackage);
          body.setAptoideUID(idsRepository.getUniqueIdentifier());
        })
        .subscribeOn(Schedulers.computation());
  }
}
