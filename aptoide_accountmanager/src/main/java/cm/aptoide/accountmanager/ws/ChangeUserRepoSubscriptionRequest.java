/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.accountmanager.ws;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.GenericResponseV3;
import rx.Observable;

/**
 * Created by neuro on 17-05-2016.
 */
public class ChangeUserRepoSubscriptionRequest extends v3accountManager<GenericResponseV3> {

  private String storeName;
  private boolean subscribe;
  private AptoideAccountManager accountManager;

  ChangeUserRepoSubscriptionRequest(AptoideAccountManager accountManager) {
    super(accountManager);
    this.accountManager = accountManager;
  }

  public static ChangeUserRepoSubscriptionRequest of(String storeName, boolean subscribe,
      AptoideAccountManager accountManager) {

    ChangeUserRepoSubscriptionRequest changeUserRepoSubscriptionRequest =
        new ChangeUserRepoSubscriptionRequest(accountManager);

    changeUserRepoSubscriptionRequest.storeName = storeName;
    changeUserRepoSubscriptionRequest.subscribe = subscribe;

    return changeUserRepoSubscriptionRequest;
  }

  @Override protected Observable<GenericResponseV3> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {

    map.put("mode", "json");
    map.put("repo", storeName);
    map.put("status", subscribe ? "subscribed" : "unsubscribed");

    map.put("access_token", accountManager.getAccessToken());

    return interfaces.changeUserRepoSubscription(map);
  }
}
