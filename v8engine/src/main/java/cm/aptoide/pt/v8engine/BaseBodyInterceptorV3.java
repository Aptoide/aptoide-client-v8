package cm.aptoide.pt.v8engine;

import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import rx.Single;
import rx.schedulers.Schedulers;

public class BaseBodyInterceptorV3 implements BodyInterceptor<BaseBody> {

  private final String aptoideMd5sum;
  private final String aptoidePackage;

  public BaseBodyInterceptorV3(String aptoideMd5sum, String aptoidePackage) {
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return Single.just(body).doOnEach(notification -> {
      body.setAptoideMd5sum(aptoideMd5sum);
      body.setAptoidePackage(aptoidePackage);
    }).subscribeOn(Schedulers.computation());
  }
}
