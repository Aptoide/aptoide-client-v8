package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import rx.Observable;

/**
 * Created by trinkes on 27/02/2017.
 */

public class GetUserRequest extends V7<GetStore, GetUserRequest.Body> {
  private static final String TAG = GetUserRequest.class.getSimpleName();
  private String url;

  public GetUserRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
    this.url = url;
  }

  public static GetUserRequest of(String url, BodyInterceptor<BaseBody> bodyInterceptor) {
    final GetUserRequest.Body body = new GetUserRequest.Body(WidgetsArgs.createDefault());
    return new GetUserRequest(new V7Url(url).remove("user/get").get(), body, bodyInterceptor);
  }

  @Override
  protected Observable<GetStore> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getUser(url, body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {
    @Getter private WidgetsArgs widgetsArgs;

    public Body(WidgetsArgs widgetsArgs) {
      this.widgetsArgs = widgetsArgs;
    }
  }
}
