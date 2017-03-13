package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.ws.v7.ChangeStoreSubscriptionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ChangeStoreSubscriptionRequest;

/**
 * Created by marcelobenites on 10/03/17.
 */

public interface AccountRequestFactory {

  public ChangeStoreSubscriptionRequest createChangeStoreSubscription(String storeName, String storeUserName,
      String sha1Password, ChangeStoreSubscriptionResponse.StoreSubscriptionState subscription,
      AptoideAccountManager accountManager);


  public SetUserRequest createSetUser(String access, AptoideAccountManager accountManager);
}
