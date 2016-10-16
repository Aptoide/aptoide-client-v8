/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class GetStoreDisplaysRequest
    extends BaseRequestWithStore<GetStoreDisplays, GetStoreDisplaysRequest.Body> {

  private String url;

  GetStoreDisplaysRequest(String url, Body body, String baseHost) {
    super(body, baseHost);
    this.url = url;
  }

  GetStoreDisplaysRequest(String url, Body body, OkHttpClient httpClient,
      Converter.Factory converterFactory, String baseHost) {
    super(body, httpClient, converterFactory, baseHost);
    this.url = url;
  }

  public static GetStoreDisplaysRequest ofAction(String url, StoreCredentials storeCredentials,
      String accessToken) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(
        new IdsRepository(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()));

    return new GetStoreDisplaysRequest(new V7Url(url).remove("getStoreDisplays").get(),
        (Body) decorator.decorate(new Body(storeCredentials), accessToken), BASE_HOST);
  }

  @Override protected Observable<GetStoreDisplays> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getStoreDisplays(url, body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithStore {

    public Body(StoreCredentials storeCredentials) {
      super(storeCredentials);
    }
  }
}
