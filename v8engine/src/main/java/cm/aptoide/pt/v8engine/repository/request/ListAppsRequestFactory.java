package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;

/**
 * Created by neuro on 03-01-2017.
 */
class ListAppsRequestFactory {

  private final AptoideClientUUID aptoideClientUUID;
  private final AptoideAccountManager accountManager;
  private final StoreCredentialsProvider storeCredentialsProvider;

  public ListAppsRequestFactory(AptoideAccountManager accountManager,
      AptoideClientUUID aptoideClientUUID) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.accountManager = accountManager;
    this.storeCredentialsProvider = new StoreCredentialsProviderImpl();
  }

  public ListAppsRequest newListAppsRequest(String url) {
    return ListAppsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url), accountManager.getAccessToken(),
        aptoideClientUUID.getUniqueIdentifier());
  }
}
