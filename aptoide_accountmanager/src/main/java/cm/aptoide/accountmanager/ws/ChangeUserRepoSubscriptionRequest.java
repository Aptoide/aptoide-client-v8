/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.accountmanager.ws;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.GenericResponseV3;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 17-05-2016.
 */
public class ChangeUserRepoSubscriptionRequest extends v3accountManager<GenericResponseV3> {

  private String storeName;
  private boolean subscribe;

  protected ChangeUserRepoSubscriptionRequest(OkHttpClient client,
      Converter.Factory converterFactory) {
    super(client, converterFactory);
  }

  public static ChangeUserRepoSubscriptionRequest of(String storeName, boolean subscribe) {
    ChangeUserRepoSubscriptionRequest changeUserRepoSubscriptionRequest =
        new ChangeUserRepoSubscriptionRequest(OkHttpClientFactory.getSingletonClient(),
            WebService.getDefaultConverter());

    changeUserRepoSubscriptionRequest.storeName = storeName;
    changeUserRepoSubscriptionRequest.subscribe = subscribe;

    return changeUserRepoSubscriptionRequest;
  }

  @Override protected Observable<GenericResponseV3> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {

    map.put("mode", "json");
    map.put("repo", storeName);
    map.put("status", subscribe ? "subscribed" : "unsubscribed");

    map.put("access_token", AptoideAccountManager.getAccessToken());

    return interfaces.changeUserRepoSubscription(map);
  }
}
