package cm.aptoide.pt.v8engine.repository;

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
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.util.StoreUtils;

/**
 * Created by neuro on 26-12-2016.
 */

public class RequestRepositoty {

  public ListAppsRequest getListApps(String url) {
    return ListAppsRequest.ofAction(url, StoreUtils.getStoreCredentialsFromUrl(url),
        AptoideAccountManager.getAccessToken(), AptoideAccountManager.getUserEmail(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID());
  }

  public ListStoresRequest getListStores(String url) {
    return ListStoresRequest.ofAction(url, AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID());
  }

  public GetStoreDisplaysRequest getStoreDisplays(String url) {
    return GetStoreDisplaysRequest.ofAction(url, StoreUtils.getStoreCredentialsFromUrl(url),
        AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID());
  }

  public GetStoreMetaRequest getStoreMeta(String url) {
    return GetStoreMetaRequest.ofAction(url, StoreUtils.getStoreCredentialsFromUrl(url),
        AptoideAccountManager.getAccessToken(), AptoideAccountManager.getUserEmail(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID());
  }

  public ListFullReviewsRequest getListFullReviews(String url, boolean refresh) {
    return ListFullReviewsRequest.ofAction(url, refresh, AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID());
  }

  public GetStoreRequest getStore(String url) {
    return GetStoreRequest.ofAction(url, StoreUtils.getStoreCredentialsFromUrl(url),
        AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID());
  }

  public GetStoreWidgetsRequest getStoreWidgets(String url) {
    return GetStoreWidgetsRequest.ofAction(url, StoreUtils.getStoreCredentialsFromUrl(url),
        AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID());
  }
}
