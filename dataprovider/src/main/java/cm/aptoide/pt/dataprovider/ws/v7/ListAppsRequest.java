/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.ListApps;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class ListAppsRequest
    extends BaseRequestWithStore<ListApps, ListAppsRequest.Body> {

  private String url;

  private ListAppsRequest(String url, Body body, String baseHost) {
    super(body, baseHost);
    this.url = url;
  }

  private ListAppsRequest(String url, Body body, Converter.Factory converterFactory,
      OkHttpClient httpClient, String baseHost) {
    super(body, httpClient, converterFactory, baseHost);
    this.url = url;
  }

  public static ListAppsRequest ofAction(String url, StoreCredentials storeCredentials,
      String accessToken, String email, String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    return new ListAppsRequest(new V7Url(url).remove("listApps").get(),
        (Body) decorator.decorate(new Body(storeCredentials), accessToken), BASE_HOST);
  }

  @Override
  protected Observable<ListApps> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.listApps(url, body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithStore
      implements Endless {

    @Getter private Integer limit;
    @Getter @Setter private int offset;

    public Body(StoreCredentials storeCredentials) {
      super(storeCredentials);
    }
  }
}
