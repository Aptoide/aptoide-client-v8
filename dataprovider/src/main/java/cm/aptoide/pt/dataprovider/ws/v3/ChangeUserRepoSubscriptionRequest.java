/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v3.BaseV3Response;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class ChangeUserRepoSubscriptionRequest extends V3<BaseV3Response> {

  public ChangeUserRepoSubscriptionRequest(BaseBody baseBody,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor);
  }

  public static ChangeUserRepoSubscriptionRequest of(String storeName, boolean subscribe,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    final BaseBody body = new BaseBody();
    body.put("mode", "json");
    body.put("repo", storeName);
    body.put("status", subscribe ? "subscribed" : "unsubscribed");
    return new ChangeUserRepoSubscriptionRequest(body, bodyInterceptor, httpClient,
        converterFactory);
  }

  @Override protected Observable<BaseV3Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.changeUserRepoSubscription(map);
  }
}
