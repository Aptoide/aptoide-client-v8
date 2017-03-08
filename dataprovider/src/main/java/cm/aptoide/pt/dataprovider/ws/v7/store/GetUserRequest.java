package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import rx.Observable;

/**
 * Created by trinkes on 27/02/2017.
 */

public class GetUserRequest extends V7<GetStore, GetUserRequest.Body> {
  private static final String TAG = GetUserRequest.class.getSimpleName();
  private String url;

  public GetUserRequest(String url, String baseHost, GetUserRequest.Body body) {
    super(body, baseHost);
    this.url = url;
  }

  public static GetUserRequest of(String url, BodyInterceptor interceptor) {
    final GetUserRequest.Body body = new GetUserRequest.Body(WidgetsArgs.createDefault());
    return new GetUserRequest(new V7Url(url).remove("user/get").get(), BASE_HOST,
        (GetUserRequest.Body) interceptor.intercept(body));
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
