package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetUserRequest;

/**
 * Created by trinkes on 27/02/2017.
 */

public class GetUserRequestFactory {

  private final BodyInterceptor bodyInterceptor;

  public GetUserRequestFactory(BodyInterceptor bodyInterceptor) {
    this.bodyInterceptor = bodyInterceptor;
  }

  public GetUserRequest newGetUser(String url) {
    return GetUserRequest.of(url, bodyInterceptor);
  }
}
