package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.aab.AppBundlesVisibilityManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 01/06/2017.
 */

public class GetAppMetaRequest extends V7<GetAppMeta, BaseBody> {
  private final String url;
  private final AppBundlesVisibilityManager appBundlesVisibilityManager;

  public GetAppMetaRequest(String baseHost, BaseBody body, String url,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      AppBundlesVisibilityManager appBundlesVisibilityManager) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
    this.url = url;
    this.appBundlesVisibilityManager = appBundlesVisibilityManager;
  }

  public static GetAppMetaRequest ofAction(String url, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      AppBundlesVisibilityManager appBundlesVisibilityManager) {

    return new GetAppMetaRequest(getHost(sharedPreferences), new BaseBody(),
        url.replace("getAppMeta", ""), bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, appBundlesVisibilityManager);
  }

  @Override
  protected Observable<GetAppMeta> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getAppMeta(bypassCache, url,
        appBundlesVisibilityManager.shouldEnableAppBundles());
  }
}
