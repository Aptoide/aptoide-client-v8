package cm.aptoide.pt.dataprovider.ws.v7.post;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class RelatedAppRequest extends V7<RelatedAppResponse, RelatedAppRequest.Body> {

  public RelatedAppRequest(Body body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static RelatedAppRequest of(String url, SharedPreferences sharedPreferences,
      OkHttpClient httpClient, Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    return new RelatedAppRequest(new RelatedAppRequest.Body(url), getHost(sharedPreferences),
        httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<RelatedAppResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getRelatedApps(bypassCache, body);
  }

  public static class Body extends BaseBody {
    private String url;

    public Body(String url) {
      this.url = url;
    }

    public String getUrl() {
      return url;
    }
  }
}
