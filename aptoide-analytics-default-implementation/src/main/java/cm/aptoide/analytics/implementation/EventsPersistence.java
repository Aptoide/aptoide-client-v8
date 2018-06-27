package cm.aptoide.analytics.implementation;

import cm.aptoide.analytics.implementation.data.Event;
import java.util.List;
import rx.Completable;
import rx.Observable;

public interface EventsPersistence {
  Completable save(Event event);

  Completable save(List<Event> events);

  Observable<List<Event>> getAll();

  Completable remove(List<Event> events);
}
