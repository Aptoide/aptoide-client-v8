package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.v8engine.BaseBodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.ListFullReviewsRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;

/**
 * Created by neuro on 03-01-2017.
 */
class ListFullReviewsRequestFactory {

  private final BodyInterceptor bodyInterceptor;

  public ListFullReviewsRequestFactory(BodyInterceptor bodyInterceptor) {
    this.bodyInterceptor = bodyInterceptor;
  }

  public ListFullReviewsRequest newListFullReviews(String url, boolean refresh,
      BaseRequestWithStore.StoreCredentials storeCredentials) {
    return ListFullReviewsRequest.ofAction(url, refresh, storeCredentials, bodyInterceptor);
  }
}
