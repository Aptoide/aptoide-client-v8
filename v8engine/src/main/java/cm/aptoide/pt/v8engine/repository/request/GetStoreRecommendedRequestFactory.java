package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetRecommendedStoresRequest;

/**
 * Created by trinkes on 21/03/2017.
 */

public class GetStoreRecommendedRequestFactory {

  private final BodyInterceptor bodyInterceptor;

  public GetStoreRecommendedRequestFactory(BodyInterceptor bodyInterceptor) {
    this.bodyInterceptor = bodyInterceptor;
  }

  public GetRecommendedStoresRequest newRecommendedStore(String url) {
    return GetRecommendedStoresRequest.ofAction(url, bodyInterceptor);
  }
}
