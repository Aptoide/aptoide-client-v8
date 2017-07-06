package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.AnalyticsBaseBody;
import cm.aptoide.pt.model.v7.base.BaseV7Response;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by jdandrade on 25/10/2016.
 */

public class AnalyticsEventRequest extends V7<BaseV7Response, AnalyticsEventRequest.Body> {

  private final String action;
  private final String name;
  private final String context;

  private AnalyticsEventRequest(Body body, String action, String name, String context,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.action = action;
    this.name = name;
    this.context = context;
  }

  public static AnalyticsEventRequest of(String eventName, String context, String action,
      Map<String, Object> data, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator, String appId,
      SharedPreferences sharedPreferences) {
    final AnalyticsEventRequest.Body body = new AnalyticsEventRequest.Body(appId, data);

    return new AnalyticsEventRequest(body, action, eventName, context, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.addEvent(name, action, context, body);
  }

  static class Body extends AnalyticsBaseBody {

    private final Map<String, Object> data;

    public Body(String aptoidePackage, Map<String, Object> data) {
      super(aptoidePackage);
      this.data = data;
    }

    public Map<String, Object> getData() {
      return data;
    }
  }
}
