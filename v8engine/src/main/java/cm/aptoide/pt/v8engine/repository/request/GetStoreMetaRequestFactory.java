package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.interfaces.AccessToken;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;

/**
 * Created by neuro on 03-01-2017.
 */

public class GetStoreMetaRequestFactory {

  private final AptoideClientUUID aptoideClientUUID;
  private final AccessToken accessToken;
  private final StoreCredentialsProvider storeCredentialsFromStoreName;

  public GetStoreMetaRequestFactory() {
    aptoideClientUUID = () -> new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext()).getAptoideClientUUID();

    accessToken = AptoideAccountManager::getAccessToken;
    storeCredentialsFromStoreName = new StoreCredentialsProviderImpl();
  }

  private GetStoreMetaRequest newGetStoreMetaRequest(String storeName) {
    return GetStoreMetaRequest.of(storeCredentialsFromStoreName.get(storeName), accessToken.get(),
        aptoideClientUUID.getAptoideClientUUID());
  }
}
