package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.GetMySubscribedStoresResponse;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 06-04-2017.
 */

public class GetMySubscribedStoresRequest
    extends V7<GetMySubscribedStoresResponse, GetMySubscribedStoresRequest.Body> {

  public GetMySubscribedStoresRequest(String accessToken, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(new Body(accessToken), getHost(sharedPreferences), httpClient, converterFactory,
        bodyInterceptor, tokenInvalidator);
  }

  @Override
  protected Observable<GetMySubscribedStoresResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getMySubscribedStores(bypassCache, body);
  }

  public static class Body extends BaseBody implements Endless {

    Sort sort;
    private Integer limit;
    private int offset;
    private Order order;
    private boolean refresh;

    public Body(String accessToken) {
      setAccessToken(accessToken);
    }

    public Sort getSort() {
      return sort;
    }

    public void setSort(Sort sort) {
      this.sort = sort;
    }

    @Override public int getOffset() {
      return offset;
    }

    @Override public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return limit;
    }

    public void setLimit(Integer limit) {
      this.limit = limit;
    }

    public Order getOrder() {
      return order;
    }

    public void setOrder(Order order) {
      this.order = order;
    }

    public boolean isRefresh() {
      return refresh;
    }

    public void setRefresh(boolean refresh) {
      this.refresh = refresh;
    }

    public enum Sort {
      added, latest, alpha, downloads, trending
    }
  }
}
