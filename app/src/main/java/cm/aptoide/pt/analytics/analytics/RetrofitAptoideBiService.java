package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import java.util.List;
import java.util.Map;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 12/01/2018.
 */

public class RetrofitAptoideBiService implements AptoideBiEventService {
  private final Service service;

  public RetrofitAptoideBiService(Service service) {
    this.service = service;
  }

  @Override public Completable send(List<Event> events) {
    System.out.println(events);
    return Observable.from(events)
        .map(event -> {
          System.out.println(event.getData());
          return service.addEvent(event.getEventName(), event.getAction()
              .name(), event.getContext(), event.getData());
        })
        .toList()
        .flatMap(observables -> Observable.merge(observables)
            .doOnNext(baseV7Response -> System.out.println(baseV7Response)))
        .toCompletable();
  }

  public interface Service {
    @POST("user/addEvent/name={name}/action={action}/context={context}")
    Observable<BaseV7Response> addEvent(@Path(value = "name") String name,
        @Path(value = "action") String action, @Path(value = "context") String context,
        @Body Map<String, Object> body);
  }
}
