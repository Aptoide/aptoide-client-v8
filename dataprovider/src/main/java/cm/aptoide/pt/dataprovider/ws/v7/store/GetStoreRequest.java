/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */

@EqualsAndHashCode(callSuper = true) public class GetStoreRequest
    extends BaseRequestWithStore<GetStore, GetStoreRequest.Body> {

  private final String url;

  private GetStoreRequest(String url, String baseHost, Body body, String email) {
    super(body, baseHost, email);
    this.url = url;
  }

  private GetStoreRequest(String url, OkHttpClient httpClient, Converter.Factory converterFactory,
      String baseHost, Body body) {
    super(body, httpClient, converterFactory, baseHost);
    this.url = url;
  }

  public static GetStoreRequest of(StoreCredentials storeCredentials, StoreContext storeContext,
      String accessToken, String email) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(
        new IdsRepository(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()));

    final Body body = new Body(storeCredentials, WidgetsArgs.createDefault());
    body.setContext(storeContext);

    return new GetStoreRequest("", BASE_HOST, (Body) decorator.decorate(body, accessToken), email);
  }

  public static GetStoreRequest ofAction(String url, StoreCredentials storeCredentials,
      String accessToken, String email) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(
        new IdsRepository(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()));

    final Body body = new Body(storeCredentials, WidgetsArgs.createDefault());

    return new GetStoreRequest(new V7Url(url).remove("getStore").get(), BASE_HOST,
        (Body) decorator.decorate(body, accessToken), email);
  }

  @Override
  protected Observable<GetStore> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getStore(url, body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithStore {

    @Getter private final WidgetsArgs widgetsArgs;
    @Getter @Setter private StoreContext context;

    public Body(StoreCredentials storeCredentials, WidgetsArgs widgetsArgs) {
      super(storeCredentials);
      this.widgetsArgs = widgetsArgs;
    }
  }
}
