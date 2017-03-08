package cm.aptoide.pt.v8engine.account;

import cm.aptoide.accountmanager.FollowStoreService;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.ChangeStoreSubscriptionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.store.ChangeStoreSubscriptionRequest;
import rx.Observable;

public class FollowStoreServiceImp implements FollowStoreService {

  @Override public Observable<ChangeStoreSubscriptionResponse> followStore(String storeName,
      String storeUserName, String sha1Password, BodyInterceptor interceptor) {
    return changeSubscription(storeName, storeUserName, sha1Password,
        ChangeStoreSubscriptionResponse.StoreSubscriptionState.SUBSCRIBED, interceptor);
  }

  @Override public Observable<ChangeStoreSubscriptionResponse> unFollowStore(String storeName,
      String storeUserName, String sha1Password, BodyInterceptor interceptor) {
    return changeSubscription(storeName, storeUserName, sha1Password,
        ChangeStoreSubscriptionResponse.StoreSubscriptionState.UNSUBSCRIBED, interceptor);
  }

  private Observable<ChangeStoreSubscriptionResponse> changeSubscription(String storeName,
      String storeUserName, String sha1Password,
      ChangeStoreSubscriptionResponse.StoreSubscriptionState subscription,
      BodyInterceptor interceptor) {
    return ChangeStoreSubscriptionRequest.of(storeName, subscription, storeUserName, sha1Password,
        interceptor).observe();
  }
}
