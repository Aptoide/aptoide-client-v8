package cm.aptoide.pt.v8engine.networking;

import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import rx.Single;
import rx.schedulers.Schedulers;

public class BaseBodyInterceptorV3 implements BodyInterceptor<BaseBody> {

  private final String aptoideMd5sum;
  private final String aptoidePackage;
  private final IdsRepository idsRepository;

  public BaseBodyInterceptorV3(String aptoideMd5sum, String aptoidePackage,
      IdsRepository idsRepository) {
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.idsRepository = idsRepository;
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
