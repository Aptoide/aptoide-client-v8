package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;

/**
 * Created by neuro on 03-01-2017.
 */
class ListStoresRequestFactory {

  private final AptoideClientUUID aptoideClientUUID;
  private final AptoideAccountManager accountManager;
  private BodyInterceptor bodyInterceptor;

  public ListStoresRequestFactory(AptoideClientUUID aptoideClientUUID,
      AptoideAccountManager accountManager, BodyInterceptor baseBodyInterceptor) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.accountManager = accountManager;
    this.bodyInterceptor = baseBodyInterceptor;
  }

  public ListStoresRequest newListStoresRequest(int offset, int limit) {
    return ListStoresRequest.ofTopStores(offset, limit, accountManager.getAccessToken(),
        bodyInterceptor);
  }

  public ListStoresRequest newListStoresRequest(String url) {
    return ListStoresRequest.ofAction(url, bodyInterceptor);
  }
}
