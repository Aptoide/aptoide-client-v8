package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by pedroribeiro on 29/06/17.
 */

public class BiUtmAnalyticsRequest extends V7<BaseV7Response, BiUtmAnalyticsRequestBody> {

  private final String action;
  private final String name;
  private final String context;

  private BiUtmAnalyticsRequest(String action, String name, String context,
      BiUtmAnalyticsRequestBody body, OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor bodyInterceptor, SharedPreferences sharedPreferences,
      TokenInvalidator tokenInvalidator) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.action = action;
    this.name = name;
    this.context = context;
  }

  public static BiUtmAnalyticsRequest of(String action, String eventName, String context,
      BiUtmAnalyticsRequestBody body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator) {
    return new BiUtmAnalyticsRequest(action, eventName, context, body, httpClient, converterFactory,
        bodyInterceptor, sharedPreferences, tokenInvalidator);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.addEvent(name, action, context, body);
  }
}
