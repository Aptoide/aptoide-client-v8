package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;

/**
 * Created by neuro on 03-01-2017.
 */
class GetStoreWidgetsRequestFactory {

  private final IdsRepository idsRepository;
  private final AptoideAccountManager accountManager;
  private final StoreCredentialsProvider storeCredentialsProvider;

  public GetStoreWidgetsRequestFactory(IdsRepository idsRepository,
      AptoideAccountManager accountManager, StoreCredentialsProvider storeCredentialsProvider) {
    this.idsRepository = idsRepository;
    this.accountManager = accountManager;
    this.storeCredentialsProvider = storeCredentialsProvider;
  }

  public GetStoreWidgetsRequest newStoreWidgets(String url) {
    return GetStoreWidgetsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url),
        accountManager.getAccessToken(), idsRepository.getAptoideClientUUID());
  }
}
