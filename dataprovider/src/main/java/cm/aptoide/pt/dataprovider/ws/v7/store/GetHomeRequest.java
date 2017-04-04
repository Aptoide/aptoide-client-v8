package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.support.annotation.Nullable;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.GetHome;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import rx.Observable;

/**
 * Created by trinkes on 23/02/2017.
 */
public class GetHomeRequest extends V7<GetHome, GetHomeBody> {

  protected GetHomeRequest(GetHomeBody body, String baseHost,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, baseHost,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static GetHomeRequest of(@Nullable BaseRequestWithStore.StoreCredentials storeCredentials,
      @Nullable Long userId, StoreContext storeContext, BodyInterceptor<BaseBody> bodyInterceptor) {
    final GetHomeBody body = new GetHomeBody(storeCredentials, WidgetsArgs.createDefault(), userId);
    body.setContext(storeContext);
    return new GetHomeRequest(body, BASE_HOST, bodyInterceptor);
  }

  @Override
  protected Observable<GetHome> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getHome(body, bypassCache);
  }
}
