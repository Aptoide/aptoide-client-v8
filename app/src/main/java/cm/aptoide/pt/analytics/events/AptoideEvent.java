package cm.aptoide.pt.analytics.events;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.AnalyticsEventRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.analytics.Event;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 06/01/2017.
 */

public class AptoideEvent implements Event {

  private final Map<String, Object> data;
  private final String eventName;
  private final String action;
  private final String context;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final String appId;
  private final SharedPreferences sharedPreferences;

  public AptoideEvent(Map<String, Object> data, String eventName, String action, String context,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator, String appId,
      SharedPreferences sharedPreferences) {
    this.data = data;
    this.eventName = eventName;
    this.action = action;
    this.context = context;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.appId = appId;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public void send() {
    AnalyticsEventRequest.of(eventName, context, action, data, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, appId, sharedPreferences)
        .observe()
        .observeOn(Schedulers.io())
        .subscribe(baseV7Response -> {
        }, throwable -> throwable.printStackTrace());
  }
}
