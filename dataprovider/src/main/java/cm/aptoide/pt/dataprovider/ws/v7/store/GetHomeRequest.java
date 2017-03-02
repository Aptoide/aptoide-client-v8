package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.support.annotation.Nullable;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.GetHome;
import rx.Observable;

/**
 * Created by trinkes on 23/02/2017.
 */
public class GetHomeRequest extends V7<GetHome, GetHomeBody> {

  protected GetHomeRequest(GetHomeBody body, String baseHost) {
    super(body, baseHost);
  }

  public static GetHomeRequest of(@Nullable BaseRequestWithStore.StoreCredentials storeCredentials,
      @Nullable Long userId, StoreContext storeContext, String accessToken,
      String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    final GetHomeBody body = new GetHomeBody(storeCredentials, WidgetsArgs.createDefault(), userId);
    body.setContext(storeContext);

    return new GetHomeRequest((GetHomeBody) decorator.decorate(body, accessToken), BASE_HOST);
  }

  @Override
  protected Observable<GetHome> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getHome(body, bypassCache);
  }
}
