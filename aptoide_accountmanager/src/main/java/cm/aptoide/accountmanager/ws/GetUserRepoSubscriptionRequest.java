/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.accountmanager.ws;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.GetUserRepoSubscription;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import java.util.HashMap;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by rmateus on 16-02-2015.
 */
public class GetUserRepoSubscriptionRequest extends v3accountManager<GetUserRepoSubscription> {

  protected GetUserRepoSubscriptionRequest(OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    super(httpClient, converterFactory);
  }

  public static GetUserRepoSubscriptionRequest of() {
    return new GetUserRepoSubscriptionRequest(OkHttpClientFactory.getSingletonClient(),
        WebService.getDefaultConverter());
  }

  @Override protected Observable<GetUserRepoSubscription> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    HashMap<String, String> parameters = new HashMap<>();

    parameters.put("mode", "json");
    parameters.put("access_token", AptoideAccountManager.getAccessToken());

    return interfaces.getUserRepos(parameters);
  }
}
