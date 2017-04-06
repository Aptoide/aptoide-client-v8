package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import rx.Observable;

/**
 * Created by trinkes on 12/12/2016.
 */

public class GetMyStoreMetaRequest extends V7<GetStoreMeta, BaseBody> {

  public GetMyStoreMetaRequest(BaseBody body, String baseHost,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, baseHost,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static GetMyStoreMetaRequest of(BodyInterceptor<BaseBody> bodyInterceptor) {
    return new GetMyStoreMetaRequest(new BaseBody(), BASE_HOST, bodyInterceptor);
  }

  @Override protected Observable<GetStoreMeta> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getMyStoreMeta(body, bypassCache);
  }
}
