package cm.aptoide.pt.dataprovider.ws.v7.post;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class CardPreviewRequest extends V7<CardPreviewResponse, CardPreviewRequest.Body> {

  protected CardPreviewRequest(CardPreviewRequest.Body body, String baseHost,
      OkHttpClient httpClient, Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static CardPreviewRequest of(String url, SharedPreferences sharedPreferences,
      OkHttpClient httpClient, Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    return new CardPreviewRequest(new Body(url), getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<CardPreviewResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getCardPreview(bypassCache, body);
  }

  public static class Body extends BaseBody {
    private final String url;

    public Body(String url) {
      this.url = url;
    }

    public String getUrl() {
      return url;
    }
  }
}
