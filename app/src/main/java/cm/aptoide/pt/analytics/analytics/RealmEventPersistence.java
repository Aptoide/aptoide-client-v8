package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.RealmEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 12/01/2018.
 */

public class RealmEventPersistence implements EventsPersistence {
  private final Database database;
  private final RealmEventMapper mapper;

  public RealmEventPersistence(Database database, RealmEventMapper mapper) {
    this.database = database;
    this.mapper = mapper;
  }

  @Override public Completable save(Event event) {
    return Completable.fromEmitter(completableEmitter -> {
      try {
        database.insert(mapper.map(event));
        completableEmitter.onCompleted();
      } catch (JsonProcessingException e) {
        completableEmitter.onError(e);
      }
    });
  }

  @Override public Observable<List<Event>> getAll() {
    return database.getAll(RealmEvent.class)
        .flatMap(realmEvents -> {
          try {
            return Observable.just(mapper.map(realmEvents));
          } catch (IOException e) {
            return Observable.error(e);
          }
        });
  }

  @Override public Completable remove(List<Event> events) {
    return Observable.from(events)
        .flatMap(event -> {
          try {
            return Observable.just(mapper.map(event));
          } catch (JsonProcessingException e) {
            return Observable.error(e);
          }
        })
        .doOnNext(realmEvent -> database.delete(RealmEvent.class, RealmEvent.PRIMARY_KEY_NAME,
            realmEvent.getTimestamp()))
        .toList()
        .toCompletable();
  }
}
