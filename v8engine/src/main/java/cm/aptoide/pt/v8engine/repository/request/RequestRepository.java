package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.interfaces.AccessToken;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import lombok.experimental.Delegate;

/**
 * Created by neuro on 26-12-2016.
 */

public class RequestRepository {

  @Delegate private final ListStoresRequestFactory listStoresRequestFactory;
  @Delegate private final ListAppsRequestFactory listAppsRequestFactory;
  @Delegate private final ListFullReviewsRequestFactory listFullReviewsRequestFactory;

  private final AptoideClientUUID aptoideClientUUID;
  private final AccessToken accessToken;
  private final StoreCredentialsProvider storeCredentialsProvider;

  public RequestRepository() {

    aptoideClientUUID = () -> new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext()).getAptoideClientUUID();

    accessToken = AptoideAccountManager::getAccessToken;
    storeCredentialsProvider = new StoreCredentialsProviderImpl();

    listStoresRequestFactory = new ListStoresRequestFactory();
    listAppsRequestFactory = new ListAppsRequestFactory();
    listFullReviewsRequestFactory = new ListFullReviewsRequestFactory();
  }

  public GetStoreRequest newStore(String url) {
    return GetStoreRequest.ofAction(url, storeCredentialsProvider.fromUrl(url), accessToken.get(),
        aptoideClientUUID.getAptoideClientUUID());
  }

  public GetStoreWidgetsRequest newStoreWidgets(String url) {
    return GetStoreWidgetsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url),
        accessToken.get(),
        aptoideClientUUID.getAptoideClientUUID());
  }
}
