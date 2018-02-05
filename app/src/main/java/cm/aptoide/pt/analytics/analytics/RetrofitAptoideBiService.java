package cm.aptoide.pt.analytics.analytics;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.AnalyticsEventRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import java.text.DateFormat;
import java.util.Date;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 12/01/2018.
 */

public class RetrofitAptoideBiService implements AptoideBiEventService {
  private DateFormat dateFormat;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;
  private String appId;
  private SharedPreferences sharedPreferences;

  public RetrofitAptoideBiService(DateFormat dateFormat, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient client, Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      String appId, SharedPreferences sharedPreferences) {
    this.dateFormat = dateFormat;
    this.bodyInterceptor = bodyInterceptor;
    httpClient = client;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.appId = appId;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public Completable send(Event event) {
    Date date = new Date(event.getTimeStamp());
    return AnalyticsEventRequest.of(event.getEventName(), event.getContext(), event.getAction()
            .name(), event.getData(), bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        appId, sharedPreferences, dateFormat.format(date))
        .observe(true, false)
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof NoNetworkConnectionException) {
            return Observable.error(throwable);
          }
          return Observable.empty();
        })
        .toCompletable();
  }
}
