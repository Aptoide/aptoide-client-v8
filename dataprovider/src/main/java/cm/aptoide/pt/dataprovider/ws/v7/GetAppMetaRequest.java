package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.GetAppMeta;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 01/06/2017.
 */

public class GetAppMetaRequest extends V7<GetAppMeta, BaseBody> {
  private final String url;

  public GetAppMetaRequest(String baseHost, BaseBody body, String url,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor);
    this.url = url;
  }

  public static GetAppMetaRequest ofAction(String url, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {

    return new GetAppMetaRequest(BASE_HOST, new BaseBody(), url.replace("getAppMeta", ""),
        bodyInterceptor, httpClient, converterFactory);
  }

  @Override
  protected Observable<GetAppMeta> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getAppMeta(bypassCache, url);
  }
}
