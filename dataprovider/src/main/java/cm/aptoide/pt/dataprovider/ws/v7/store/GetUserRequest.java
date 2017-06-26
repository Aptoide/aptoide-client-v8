package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 27/02/2017.
 */

public class GetUserRequest extends V7<GetStore, GetUserRequest.Body> {
  private static final String TAG = GetUserRequest.class.getSimpleName();
  private String url;

  public GetUserRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.url = url;
  }

  public static GetUserRequest of(String url, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, Resources resources,
      WindowManager windowManager) {
    final GetUserRequest.Body body =
        new GetUserRequest.Body(WidgetsArgs.createDefault(resources, windowManager));
    return new GetUserRequest(new V7Url(url).remove("user/get")
        .get(), body, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences);
  }

  @Override
  protected Observable<GetStore> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getUser(url, body, bypassCache);
  }

  public static class Body extends BaseBody {
    private WidgetsArgs widgetsArgs;

    public Body(WidgetsArgs widgetsArgs) {
      this.widgetsArgs = widgetsArgs;
    }

    public WidgetsArgs getWidgetsArgs() {
      return widgetsArgs;
    }
  }
}
