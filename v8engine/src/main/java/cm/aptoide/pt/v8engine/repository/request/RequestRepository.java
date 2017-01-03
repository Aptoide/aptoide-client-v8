package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListFullReviewsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreDisplaysRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.interfaces.AccessToken;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsFromUrl;
import cm.aptoide.pt.v8engine.util.StoreUtils;

/**
 * Created by neuro on 26-12-2016.
 */

public class RequestRepository {

  private final ListStoresRequestFactory listStoresRequestFactory;
  private AptoideClientUUID aptoideClientUUID;
  private AccessToken accessToken;
  private StoreCredentialsFromUrl storeCredentialsFromUrl;

  public RequestRepository() {

    aptoideClientUUID = () -> new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext()).getAptoideClientUUID();

    accessToken = AptoideAccountManager::getAccessToken;
    storeCredentialsFromUrl = StoreUtils::getStoreCredentialsFromUrl;

    listStoresRequestFactory = new ListStoresRequestFactory();
  }

  public ListStoresRequest newListStoresRequest(int offset, int limit) {
    return listStoresRequestFactory.newListStoresRequest(offset, limit);
  }

  public ListStoresRequest newListStoresRequest(String url) {
    return listStoresRequestFactory.newListStoresRequest(url);
  }

  public ListAppsRequest newListApps(String url) {
    return ListAppsRequest.ofAction(url, storeCredentialsFromUrl.get(url), accessToken.get(),
        aptoideClientUUID.getAptoideClientUUID());
  }

  public GetStoreDisplaysRequest newStoreDisplays(String url) {
    return GetStoreDisplaysRequest.ofAction(url, storeCredentialsFromUrl.get(url),
        accessToken.get(), aptoideClientUUID.getAptoideClientUUID());
  }

  public GetStoreMetaRequest newStoreMeta(String url) {
    return GetStoreMetaRequest.ofAction(url, storeCredentialsFromUrl.get(url), accessToken.get(),
        aptoideClientUUID.getAptoideClientUUID());
  }

  public ListFullReviewsRequest newListFullReviews(String url, boolean refresh) {
    return ListFullReviewsRequest.ofAction(url, refresh, accessToken.get(),
        aptoideClientUUID.getAptoideClientUUID());
  }

  public GetStoreRequest newStore(String url) {
    return GetStoreRequest.ofAction(url, storeCredentialsFromUrl.get(url), accessToken.get(),
        aptoideClientUUID.getAptoideClientUUID());
  }

  public GetStoreWidgetsRequest newStoreWidgets(String url) {
    return GetStoreWidgetsRequest.ofAction(url, storeCredentialsFromUrl.get(url), accessToken.get(),
        aptoideClientUUID.getAptoideClientUUID());
  }
}
