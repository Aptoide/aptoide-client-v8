package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.model.v7.BaseV7Response;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 30/12/2016.
 */

public class DownloadAnalyticsRequest extends V7<BaseV7Response, DownloadInstallAnalyticsBaseBody> {

  private String action;
  private String name;
  private String context;

  protected DownloadAnalyticsRequest(DownloadInstallAnalyticsBaseBody body, String action,
      String name, String context, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
    this.action = action;
    this.name = name;
    this.context = context;
  }

  public static DownloadAnalyticsRequest of(DownloadInstallAnalyticsBaseBody body, String action,
      String name, String context, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    return new DownloadAnalyticsRequest(body, action, name, context, bodyInterceptor, httpClient,
        converterFactory);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.addEvent(name, action, context, body);
  }
}
