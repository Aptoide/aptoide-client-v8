package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.ws.v7.ChangeStoreSubscriptionResponse;
import rx.Observable;

/**
 * Created by trinkes on 03/03/2017.
 */

public interface FollowStoreService {

  Observable<ChangeStoreSubscriptionResponse> followStore(String storeName,
      String aptoideClientUUID, String accessToken);

  Observable<ChangeStoreSubscriptionResponse> followStore(String storeName,
      String aptoideClientUUID, String accessToken, String storeUserName, String sha1Password);
}
