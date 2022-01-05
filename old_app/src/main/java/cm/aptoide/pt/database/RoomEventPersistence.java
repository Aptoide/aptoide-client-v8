package cm.aptoide.pt.database;

import cm.aptoide.analytics.implementation.EventsPersistence;
import cm.aptoide.analytics.implementation.data.Event;
import cm.aptoide.pt.database.room.EventDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.List;

public class RoomEventPersistence implements EventsPersistence {
  private final EventDAO eventDAO;
  private final RoomEventMapper mapper;

  public RoomEventPersistence(EventDAO eventDAO, RoomEventMapper mapper) {
    this.eventDAO = eventDAO;
    this.mapper = mapper;
  }

  @Override public rx.Completable save(Event event) {
    return RxJavaInterop.toV1Completable(Completable.create(completableEmitter -> {
      try {
        eventDAO.insert(mapper.map(event));
        completableEmitter.onComplete();
      } catch (JsonProcessingException e) {
        completableEmitter.onError(e);
      }
    })
        .subscribeOn(Schedulers.io()));
  }

  @Override public rx.Completable save(List<Event> events) {
    return RxJavaInterop.toV1Completable(Observable.fromIterable(events)
        .flatMapCompletable(event -> RxJavaInterop.toV2Completable(save(event))));
  }

  @Override public rx.Observable<List<Event>> getAll() {
    return RxJavaInterop.toV1Observable(eventDAO.getAll()
        .subscribeOn(Schedulers.io())
        .flatMap(roomEvents -> {
          try {
            return Observable.just(mapper.map(roomEvents));
          } catch (IOException e) {
            return Observable.error(e);
          }
        }), BackpressureStrategy.BUFFER);
  }

  @Override public rx.Completable remove(List<Event> events) {
    return RxJavaInterop.toV1Completable(Observable.fromIterable(events)
        .flatMap(event -> {
          try {
            return Observable.just(mapper.map(event));
          } catch (JsonProcessingException e) {
            return Observable.error(e);
          }
        })
        .subscribeOn(Schedulers.io())
        .doOnNext(roomEvent -> eventDAO.delete(roomEvent))
        .toList()
        .ignoreElement());
  }
}

