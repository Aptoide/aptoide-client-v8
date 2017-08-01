package cm.aptoide.pt.account;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.networking.IdsRepository;
import rx.Single;
import rx.schedulers.Schedulers;

public class NoTokenBodyInterceptor implements BodyInterceptor<BaseBody> {

  private final String aptoideMd5sum;
  private final String aptoidePackage;
  private final IdsRepository idsRepository;

  public NoTokenBodyInterceptor(IdsRepository idsRepository, String aptoideMd5sum,
      String aptoidePackage) {
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.idsRepository = idsRepository;
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return Single.fromCallable(() -> {
      body.setAptoideMd5sum(aptoideMd5sum);
      body.setAptoidePackage(aptoidePackage);
      body.setAptoideUid(idsRepository.getUniqueIdentifier());
      return body;
    })
        .subscribeOn(Schedulers.computation());
  }
}
