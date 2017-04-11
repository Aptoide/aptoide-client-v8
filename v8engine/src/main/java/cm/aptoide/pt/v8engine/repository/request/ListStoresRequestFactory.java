package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;

/**
 * Created by neuro on 03-01-2017.
 */
class ListStoresRequestFactory {

  private BodyInterceptor<BaseBody> bodyInterceptor;

  public ListStoresRequestFactory(BodyInterceptor<BaseBody> baseBodyInterceptor) {
    this.bodyInterceptor = baseBodyInterceptor;
  }

  public ListStoresRequest newListStoresRequest(int offset, int limit) {
    return ListStoresRequest.ofTopStores(offset, limit, bodyInterceptor);
  }

  public ListStoresRequest newListStoresRequest(String url) {
    return ListStoresRequest.ofAction(url, bodyInterceptor);
  }
}
