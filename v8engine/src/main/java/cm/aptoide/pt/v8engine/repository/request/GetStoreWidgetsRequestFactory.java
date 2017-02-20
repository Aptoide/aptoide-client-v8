package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;

/**
 * Created by neuro on 03-01-2017.
 */
class GetStoreWidgetsRequestFactory {

  private final AptoideClientUUID aptoideClientUUID;
  private final AptoideAccountManager accountManager;
  private final StoreCredentialsProvider storeCredentialsProvider;

  public GetStoreWidgetsRequestFactory(AptoideClientUUID aptoideClientUUID,
      AptoideAccountManager accountManager, StoreCredentialsProvider storeCredentialsProvider) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.accountManager = accountManager;
    this.storeCredentialsProvider = storeCredentialsProvider;
  }

  public GetStoreWidgetsRequest newStoreWidgets(String url) {
    return GetStoreWidgetsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url),
        accountManager.getAccessToken(), aptoideClientUUID.getUniqueIdentifier());
  }
}
