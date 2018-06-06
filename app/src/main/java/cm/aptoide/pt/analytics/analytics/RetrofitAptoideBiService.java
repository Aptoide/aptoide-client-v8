package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.analytics.implementation.AptoideBiEventService;
import cm.aptoide.analytics.implementation.Event;
import java.text.DateFormat;
import java.util.Date;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 12/01/2018.
 */

public class RetrofitAptoideBiService implements AptoideBiEventService {
  private final ServiceV7 serviceV7;
  private final AnalyticsBodyInterceptor analyticsBodyInterceptor;
  private DateFormat dateFormat;

  public RetrofitAptoideBiService(DateFormat dateFormat, ServiceV7 serviceV7,
      AnalyticsBodyInterceptor analyticsBodyInterceptor) {
    this.dateFormat = dateFormat;
    this.serviceV7 = serviceV7;
    this.analyticsBodyInterceptor = analyticsBodyInterceptor;
  }

  @Override public Completable send(Event event) {
    AnalyticsEventRequestBody body = new AnalyticsEventRequestBody(event.getData(),
        dateFormat.format(new Date(event.getTimeStamp())));
    return analyticsBodyInterceptor.intercept(body)
        .flatMapCompletable(analyticsBody -> serviceV7.sendEvent(event.getEventName(),
            event.getAction()
                .name(), event.getContext(), (AnalyticsEventRequestBody) analyticsBody)
            .onErrorResumeNext(throwable -> {
              if (throwable instanceof IllegalStateException) {
                return Observable.error(throwable);
              }
              return Observable.empty();
            })
            .toCompletable());
  }

  public interface ServiceV7 {
    @POST("user/addEvent/name={name}/action={action}/context={context}")
    Observable<Response<Void>> sendEvent(@Path(value = "name") String name,
        @Path(value = "action") String action, @Path(value = "context") String context,
        @Body AnalyticsEventRequestBody body);
  }
}
