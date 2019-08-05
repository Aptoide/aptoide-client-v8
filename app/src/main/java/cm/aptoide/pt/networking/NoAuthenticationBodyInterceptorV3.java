package cm.aptoide.pt.networking;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.preferences.AptoideMd5Manager;
import rx.Single;
import rx.schedulers.Schedulers;

public class NoAuthenticationBodyInterceptorV3 implements BodyInterceptor<BaseBody> {

  private final AptoideMd5Manager aptoideMd5Manager;
  private final String aptoidePackage;
  private final IdsRepository idsRepository;

  public NoAuthenticationBodyInterceptorV3(IdsRepository idsRepository,
      AptoideMd5Manager aptoideMd5Manager, String aptoidePackage) {
    this.aptoideMd5Manager = aptoideMd5Manager;
    this.aptoidePackage = aptoidePackage;
    this.idsRepository = idsRepository;
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return Single.fromCallable(() -> {
      String md5 = aptoideMd5Manager.getAptoideMd5();
      if (!md5.equals("")) body.setAptoideMd5sum(md5);
      body.setAptoidePackage(aptoidePackage);
      body.setAptoideUid(idsRepository.getUniqueIdentifier());
      return body;
    })
        .subscribeOn(Schedulers.computation());
  }
}
