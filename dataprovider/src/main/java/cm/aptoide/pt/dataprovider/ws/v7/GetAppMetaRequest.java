package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 01/06/2017.
 */

public class GetAppMetaRequest extends V7<GetApp, BaseBody> {
  private final String url;

  public GetAppMetaRequest(String baseHost, BaseBody body, String url,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
    this.url = url;
  }

  public static GetAppMetaRequest ofAction(String url, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {

    return new GetAppMetaRequest(getHost(sharedPreferences), new BaseBody(), url, bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator);
  }

  @Override
  protected Observable<GetApp> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getAppMeta(bypassCache, url);
  }
}
