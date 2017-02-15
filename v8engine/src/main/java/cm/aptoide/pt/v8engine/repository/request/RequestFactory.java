package cm.aptoide.pt.v8engine.repository.request;

import lombok.experimental.Delegate;

/**
 * Created by neuro on 26-12-2016.
 */

public class RequestFactory {

  @Delegate private final ListStoresRequestFactory listStoresRequestFactory;
  @Delegate private final ListAppsRequestFactory listAppsRequestFactory;
  @Delegate private final ListFullReviewsRequestFactory listFullReviewsRequestFactory;
  @Delegate private final GetStoreRequestFactory getStoreRequestFactory;
  @Delegate private final GetStoreWidgetsRequestFactory getStoreWidgetsRequestFactory;

  public RequestFactory() {
    listStoresRequestFactory = new ListStoresRequestFactory();
    listAppsRequestFactory = new ListAppsRequestFactory();
    listFullReviewsRequestFactory = new ListFullReviewsRequestFactory();
    getStoreRequestFactory = new GetStoreRequestFactory();
    getStoreWidgetsRequestFactory = new GetStoreWidgetsRequestFactory();
  }
}
