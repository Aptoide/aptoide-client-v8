package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.v7.V7EndlessController;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.model.v7.store.Store;

/**
 * Created by neuro on 03-01-2017.
 */
class ListStoresRequestFactory {

  private final IdsRepository idsRepository;
  private final AptoideAccountManager accountManager;

  public ListStoresRequestFactory(IdsRepository idsRepository,
      AptoideAccountManager accountManager) {
    this.idsRepository = idsRepository;
    this.accountManager = accountManager;
  }

  public ListStoresRequest newListStoresRequest(int offset, int limit) {
    return ListStoresRequest.ofTopStores(offset, limit, accountManager.getAccessToken(),
        idsRepository.getAptoideClientUUID());
  }

  public V7EndlessController<Store> listStores(int offset, int limit) {
    return new V7EndlessController<>(
        ListStoresRequest.ofTopStores(offset, limit, accountManager.getAccessToken(),
            idsRepository.getAptoideClientUUID()));
  }

  public ListStoresRequest newListStoresRequest(String url) {
    return ListStoresRequest.ofAction(url, accountManager.getAccessToken(),
        idsRepository.getAptoideClientUUID());
  }
}
