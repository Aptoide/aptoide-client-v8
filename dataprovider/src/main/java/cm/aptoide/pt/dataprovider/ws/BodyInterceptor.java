package cm.aptoide.pt.dataprovider.ws;

import rx.Single;

public interface BodyInterceptor<T> {

  public Single<T> intercept(T body);
}
