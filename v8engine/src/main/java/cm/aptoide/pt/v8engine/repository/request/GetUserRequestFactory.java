package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetUserRequest;
import cm.aptoide.pt.interfaces.AccessToken;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;

/**
 * Created by trinkes on 27/02/2017.
 */

public class GetUserRequestFactory {

  private final AptoideClientUUID aptoideClientUUID;
  private final AccessToken accessToken;

  public GetUserRequestFactory() {
    aptoideClientUUID = () -> new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext()).getUniqueIdentifier();

    accessToken = AptoideAccountManager::getAccessToken;
  }

  public GetUserRequest newGetUser(String url) {
    return GetUserRequest.of(url, accessToken.get(), aptoideClientUUID.getUniqueIdentifier());
  }
}
