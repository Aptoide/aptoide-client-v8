/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import io.realm.Sort;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by sithengineer on 01/09/16.
 */
public class RollbackAccessor implements Accessor {

  private final Database database;

  protected RollbackAccessor(Database db) {
    this.database = db;
  }

  public Observable<List<Rollback>> getAll() {
    return database.getAll(Rollback.class);
  }

  public Observable<List<Rollback>> getAllSorted(Sort sort) {
    return Observable.fromCallable(() -> Database.get())
        .flatMap(realm -> realm.where(Rollback.class)
            .findAllSorted(Rollback.TIMESTAMP, sort)
            .asObservable()
            .unsubscribeOn(RealmSchedulers.getScheduler()))
        .flatMap(rollbacks -> database.copyFromRealm(rollbacks))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }
}
