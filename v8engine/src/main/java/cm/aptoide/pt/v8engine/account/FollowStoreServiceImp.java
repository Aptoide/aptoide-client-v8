package cm.aptoide.pt.v8engine.account;

import cm.aptoide.accountmanager.FollowStoreService;
import cm.aptoide.pt.dataprovider.ws.v7.ChangeStoreSubscriptionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.store.ChangeStoreSubscriptionRequest;
import rx.Observable;

/**
 * Created by trinkes on 03/03/2017.
 */

public class FollowStoreServiceImp implements FollowStoreService {
  private static final String TAG = FollowStoreServiceImp.class.getSimpleName();

  @Override public Observable<ChangeStoreSubscriptionResponse> followStore(String storeName,
      String aptoideClientUUID, String accessToken) {
    return ChangeStoreSubscriptionRequest.of(storeName,
        ChangeStoreSubscriptionResponse.StoreSubscriptionState.SUBSCRIBED, aptoideClientUUID,
        accessToken).observe();
  }

  @Override public Observable<ChangeStoreSubscriptionResponse> followStore(String storeName,
      String aptoideClientUUID, String accessToken, String storeUserName, String sha1Password) {
    return changeSubscription(storeName, aptoideClientUUID, accessToken, storeUserName,
        sha1Password, ChangeStoreSubscriptionResponse.StoreSubscriptionState.SUBSCRIBED);
  }

  @Override public Observable<ChangeStoreSubscriptionResponse> unFollowStore(String storeName,
      String aptoideClientUUID, String accessToken, String storeUserName, String sha1Password) {
    return changeSubscription(storeName, aptoideClientUUID, accessToken, storeUserName,
        sha1Password, ChangeStoreSubscriptionResponse.StoreSubscriptionState.UNSUBSCRIBED);
  }

  private Observable<ChangeStoreSubscriptionResponse> changeSubscription(String storeName,
      String aptoideClientUUID, String accessToken, String storeUserName, String sha1Password,
      ChangeStoreSubscriptionResponse.StoreSubscriptionState subscription) {
    return ChangeStoreSubscriptionRequest.of(storeName, subscription, aptoideClientUUID,
        accessToken, storeUserName, sha1Password).observe();
  }
}
