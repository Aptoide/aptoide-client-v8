package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.ListStores;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 12/12/2016.
 */

public class GetMyStoreListRequest extends V7<ListStores, GetMyStoreListRequest.EndlessBody> {

  private static boolean useEndless;
  @Nullable private String url;

  public GetMyStoreListRequest(String url, EndlessBody body,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.url = url;
  }

  public static GetMyStoreListRequest of(String url, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, Resources resources,
      WindowManager windowManager) {
    return of(url, false, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences, resources, windowManager);
  }

  public static GetMyStoreListRequest of(String url, boolean useEndless,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources, WindowManager windowManager) {
    GetMyStoreListRequest.useEndless = useEndless;

    return new GetMyStoreListRequest(url,
        new EndlessBody(WidgetsArgs.createDefault(resources, windowManager)), bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<ListStores> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    if (url.contains("getSubscribed")) {
      body.setRefresh(bypassCache);
    }
    if (TextUtils.isEmpty(url)) {
      return interfaces.getMyStoreList(body, bypassCache);
    } else {
      if (useEndless) {
        return interfaces.getMyStoreListEndless(url, body, bypassCache);
      } else {
        return interfaces.getMyStoreList(url, body, bypassCache);
      }
    }
  }

  public static class EndlessBody extends Body implements Endless {

    private int offset;
    private Integer limit = 25;

    public EndlessBody(WidgetsArgs widgetsArgs) {
      super(widgetsArgs);
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
  }

  public static class Body extends BaseBody {
    private boolean refresh;
    private WidgetsArgs widgetsArgs;

    public Body(WidgetsArgs widgetsArgs) {
      super();
      this.widgetsArgs = widgetsArgs;
    }

    public boolean isRefresh() {
      return refresh;
    }

    public void setRefresh(boolean refresh) {
      this.refresh = refresh;
    }

    public WidgetsArgs getWidgetsArgs() {
      return widgetsArgs;
    }
  }
}
