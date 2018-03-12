package cm.aptoide.pt.dataprovider.ws.v7.home;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BundlesEndlessDataListResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class GetHomeBundlesRequest
    extends V7<BundlesEndlessDataListResponse, GetHomeBundlesRequest.Body> {
  protected GetHomeBundlesRequest(Body body, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static GetHomeBundlesRequest of(int limit, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    return new GetHomeBundlesRequest(new Body(limit), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<BundlesEndlessDataListResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getHomeBundles(body, bypassCache);
  }

  public static class Body extends BaseBody implements Endless {

    private StoreContext context;
    private Integer limit;
    private long storeId;
    private int offset;

    public Body(Integer limit) {
      this.limit = limit;
      this.context = StoreContext.home;
      this.storeId = 15;
    }

    public StoreContext getContext() {
      return context;
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

    public long getStoreId() {
      return storeId;
    }

    public void setStoreId(long storeId) {
      this.storeId = storeId;
    }
  }
}
