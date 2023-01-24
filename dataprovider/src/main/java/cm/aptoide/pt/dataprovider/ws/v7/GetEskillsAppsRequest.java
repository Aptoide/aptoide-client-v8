package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.aab.AppBundlesVisibilityManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class GetEskillsAppsRequest extends V7<ListApps, BaseBody> {

  private final AppBundlesVisibilityManager appBundlesVisibilityManager;
  private final String url;

  public GetEskillsAppsRequest(String url, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      AppBundlesVisibilityManager appBundlesVisibilityManager) {
    super(new BaseBody(), getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.appBundlesVisibilityManager = appBundlesVisibilityManager;
    if (url != null && url.contains("listApps")) {
      url = url.split("listApps/")[1];
    } this.url = url;
  }

  @Override
  protected Observable<ListApps> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getEskillsApps(bypassCache, url,
        appBundlesVisibilityManager.shouldEnableAppBundles());
  }
}
