package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.v7.BodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListFullReviewsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7EndlessController;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.BaseBodyDecorator;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;

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

  public RequestFactory(AptoideClientUUID aptoideClientUUID, AptoideAccountManager accountManager,
      StoreCredentialsProvider storeCredentialsProvider, BodyDecorator bodyDecorator) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    listStoresRequestFactory =
        new ListStoresRequestFactory(aptoideClientUUID, accountManager, bodyDecorator);
    listAppsRequestFactory =
        new ListAppsRequestFactory(bodyDecorator, storeCredentialsProvider);
    listFullReviewsRequestFactory =
        new ListFullReviewsRequestFactory(aptoideClientUUID, accountManager, bodyDecorator);
    getStoreRequestFactory =
        new GetStoreRequestFactory(accountManager, storeCredentialsProvider, bodyDecorator);
    getStoreWidgetsRequestFactory =
        new GetStoreWidgetsRequestFactory(accountManager, storeCredentialsProvider, bodyDecorator);
  }

  public ListStoresRequest newListStoresRequest(int offset, int limit) {
    return this.listStoresRequestFactory.newListStoresRequest(offset, limit);
  }

  public V7EndlessController<Store> listStores(int offset, int limit) {
    return this.listStoresRequestFactory.listStores(offset, limit);
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
}
