package cm.aptoide.pt.v8engine.account;

import cm.aptoide.accountmanager.AccountRequestFactory;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.ChangeStoreSubscriptionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ChangeStoreSubscriptionRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.v8engine.BaseBodyInterceptor;

public class AccountDynamicRequestFactory implements AccountRequestFactory {

  private final AptoideClientUUID aptoideClientUUID;

  public AccountDynamicRequestFactory(AptoideClientUUID aptoideClientUUID) {
    this.aptoideClientUUID = aptoideClientUUID;
  }

  @Override public ChangeStoreSubscriptionRequest createChangeStoreSubscription(String storeName,
      String storeUserName, String sha1Password,
      ChangeStoreSubscriptionResponse.StoreSubscriptionState subscription,
      AptoideAccountManager accountManager) {
    return ChangeStoreSubscriptionRequest.of(storeName, subscription, storeUserName, sha1Password,
        new BaseBodyInterceptor(aptoideClientUUID, accountManager));
  }

  @Override
  public SetUserRequest createSetUser(String access, AptoideAccountManager accountManager) {
    return SetUserRequest.of(access, new BaseBodyInterceptor(aptoideClientUUID, accountManager));
  }
}