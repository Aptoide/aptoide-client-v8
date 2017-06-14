package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.ListStores;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
    this.url = url;
  }

  public static GetMyStoreListRequest of(String url, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator) {
    return of(url, false, bodyInterceptor, httpClient, converterFactory, tokenInvalidator);
  }

  public static GetMyStoreListRequest of(String url, boolean useEndless,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator) {
    GetMyStoreListRequest.useEndless = useEndless;

    return new GetMyStoreListRequest(url, new EndlessBody(WidgetsArgs.createDefault()),
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator);
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

  @EqualsAndHashCode(callSuper = true) public static class EndlessBody extends Body
      implements Endless {

    @Getter private Integer limit = 25;
    @Getter @Setter private int offset;

    public EndlessBody(WidgetsArgs widgetsArgs) {
      super(widgetsArgs);
    }
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {
    @Getter private WidgetsArgs widgetsArgs;
    @Getter @Setter private boolean refresh;

    public Body(WidgetsArgs widgetsArgs) {
      super();
      this.widgetsArgs = widgetsArgs;
    }
  }
}
