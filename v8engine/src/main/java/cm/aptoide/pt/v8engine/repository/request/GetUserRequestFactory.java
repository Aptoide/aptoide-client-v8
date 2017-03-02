package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetUserRequest;

/**
 * Created by trinkes on 27/02/2017.
 */

public class GetUserRequestFactory {

  private final IdsRepository idsRepository;
  private final AptoideAccountManager accountManager;

  public GetUserRequestFactory(IdsRepository idsRepository, AptoideAccountManager accountManager) {
    this.idsRepository = idsRepository;
    this.accountManager = accountManager;
  }

  public GetUserRequest newGetUser(String url) {
    return GetUserRequest.of(url, accountManager.getAccessToken(),
        idsRepository.getUniqueIdentifier());
  }
}
