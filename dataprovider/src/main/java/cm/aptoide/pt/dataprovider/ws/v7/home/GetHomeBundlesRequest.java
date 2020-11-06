package cm.aptoide.pt.dataprovider.ws.v7.home;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.dataprovider.ws.v7.store.WidgetsArgs;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class GetHomeBundlesRequest extends V7<GetStoreWidgets, GetHomeBundlesRequest.Body> {

  private GetHomeBundlesRequest(Body body, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static GetHomeBundlesRequest of(int limit, int offset, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, Resources resources,
      WindowManager windowManager) {
    return new GetHomeBundlesRequest(
        new Body(limit, offset, WidgetsArgs.createDefault(resources, windowManager)), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator, sharedPreferences);
  }

  @Override
  public Observable<GetStoreWidgets> observe(boolean bypassCache, boolean bypassServerCache) {
    return super.observe(bypassCache, bypassServerCache);
  }

  @Override protected Observable<GetStoreWidgets> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return Observable.just(null)
        .flatMap(__ -> interfaces.getHomeBundles(body, bypassCache));
  }

  public static class Body extends BaseBody implements Endless {

    private WidgetsArgs widgetsArgs;
    private StoreContext context;
    private Integer limit;
    private long storeId;
    private int offset;

    public Body(Integer limit, int offset, WidgetsArgs widgetsArgs) {
      this.limit = limit;
      this.offset = offset;
      this.widgetsArgs = widgetsArgs;
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

    public WidgetsArgs getWidgetsArgs() {
      return widgetsArgs;
    }

    public void setWidgetsArgs(WidgetsArgs widgetsArgs) {
      this.widgetsArgs = widgetsArgs;
    }
  }
}
