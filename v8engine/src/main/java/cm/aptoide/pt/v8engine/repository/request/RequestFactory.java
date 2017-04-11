package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListFullReviewsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetRecommendedStoresRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetUserRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;

/**
 * Created by neuro on 26-12-2016.
 */

public class RequestFactory {

  private final ListStoresRequestFactory listStoresRequestFactory;
  private final ListAppsRequestFactory listAppsRequestFactory;
  private final ListFullReviewsRequestFactory listFullReviewsRequestFactory;
  private final GetStoreRequestFactory getStoreRequestFactory;
  private final GetStoreWidgetsRequestFactory getStoreWidgetsRequestFactory;
  private final StoreCredentialsProvider storeCredentialsProvider;
  private final GetUserRequestFactory getUserRequestFactory;
  private final GetStoreRecommendedRequestFactory getStoreRecommendedRequestFactory;

  public RequestFactory(StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    listStoresRequestFactory = new ListStoresRequestFactory(bodyInterceptor);
    listAppsRequestFactory = new ListAppsRequestFactory(bodyInterceptor, storeCredentialsProvider);
    listFullReviewsRequestFactory = new ListFullReviewsRequestFactory(bodyInterceptor);
    getStoreRequestFactory = new GetStoreRequestFactory(storeCredentialsProvider, bodyInterceptor);
    getStoreWidgetsRequestFactory =
        new GetStoreWidgetsRequestFactory(storeCredentialsProvider, bodyInterceptor);
    getUserRequestFactory = new GetUserRequestFactory(bodyInterceptor);

    getStoreRecommendedRequestFactory = new GetStoreRecommendedRequestFactory(bodyInterceptor);
  }

  public ListStoresRequest newListStoresRequest(int offset, int limit) {
    return this.listStoresRequestFactory.newListStoresRequest(offset, limit);
  }

  public ListStoresRequest newListStoresRequest(String url) {
    return this.listStoresRequestFactory.newListStoresRequest(url);
  }

  public ListAppsRequest newListAppsRequest(String url) {
    return this.listAppsRequestFactory.newListAppsRequest(url);
  }

  public ListFullReviewsRequest newListFullReviews(String url, boolean refresh) {
    return this.listFullReviewsRequestFactory.newListFullReviews(url, refresh,
        storeCredentialsProvider.fromUrl(url));
  }

  public GetStoreRequest newStore(String url) {
    return this.getStoreRequestFactory.newStore(url);
  }

  public GetStoreWidgetsRequest newStoreWidgets(String url) {
    return this.getStoreWidgetsRequestFactory.newStoreWidgets(url);
  }

  public GetUserRequest newGetUser(String url) {
    return this.getUserRequestFactory.newGetUser(url);
  }

  public GetRecommendedStoresRequest newGetRecommendedStores(String url) {
    return this.getStoreRecommendedRequestFactory.newRecommendedStore(url);
  }
}
