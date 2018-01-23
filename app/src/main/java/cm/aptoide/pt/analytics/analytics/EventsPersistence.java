package cm.aptoide.pt.analytics.analytics;

import java.util.List;
import rx.Completable;
import rx.Observable;

public interface EventsPersistence {
  Completable save(Event event);

  Completable save(List<Event> events);

  Observable<List<Event>> getAll();

  Completable remove(List<Event> events);
}
