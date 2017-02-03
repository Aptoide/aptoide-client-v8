package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;

/**
 * Created by neuro on 03-01-2017.
 */
class ListAppsRequestFactory {

  private final IdsRepository idsRepository;
  private final AptoideAccountManager accessToken;
  private final StoreCredentialsProvider storeCredentialsProvider;

  public ListAppsRequestFactory(AptoideAccountManager accountManager,
      IdsRepository idsRepository) {
    this.idsRepository = idsRepository;
    accessToken = accountManager;
    storeCredentialsProvider = new StoreCredentialsProviderImpl();
  }

  public ListAppsRequest newListAppsRequest(String url) {
    return ListAppsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url), accessToken.getAccessToken(),
        idsRepository.getAptoideClientUUID());
  }
}
