package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.model.v7.store.GetHome;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 23/02/2017.
 */
public class GetHomeRequest extends V7<GetHome, GetHomeBody> {

  protected GetHomeRequest(GetHomeBody body, String baseHost,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static GetHomeRequest of(@Nullable BaseRequestWithStore.StoreCredentials storeCredentials,
      @Nullable Long userId, StoreContext storeContext, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, Resources resources,
      WindowManager windowManager) {
    final GetHomeBody body =
        new GetHomeBody(storeCredentials, WidgetsArgs.createDefault(resources, windowManager),
            userId);
    body.setContext(storeContext);
    return new GetHomeRequest(body, getHost(sharedPreferences), bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator);
  }

  @Override
  protected Observable<GetHome> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getHome(body, bypassCache);
  }
}
