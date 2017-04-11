package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by trinkes on 21/03/2017.
 */

public class GetRecommendedStoresRequest
    extends V7<ListStores, GetRecommendedStoresRequest.EndlessBody> {

  private final String url;

  public GetRecommendedStoresRequest(String url, EndlessBody body,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
    this.url = url;
  }

  public static GetRecommendedStoresRequest ofAction(String url,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    return new GetRecommendedStoresRequest(url, new EndlessBody(), bodyInterceptor);
  }

  @Override
  protected Observable<ListStores> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getRecommendedStore(url, body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class EndlessBody extends BaseBody
      implements Endless {

    private int limit = 25;
    private int offset;

    @Override public int getOffset() {
      return offset;
    }

    @Override public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return limit;
    }
  }
}
