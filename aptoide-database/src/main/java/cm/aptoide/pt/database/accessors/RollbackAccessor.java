/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import io.realm.Sort;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created on 01/09/16.
 */
public class RollbackAccessor extends SimpleAccessor<Rollback> {

  public RollbackAccessor(Database db) {
    super(db, Rollback.class);
  }

  public Observable<List<Rollback>> getAll() {
    return database.getAll(Rollback.class);
  }

  //public Observable<List<Rollback>> getAllSorted(Sort sort) {
  //  return Observable.fromCallable(() -> Database.get())
  //      .flatMap(realm -> realm.where(Rollback.class)
  //          .findAllSorted(Rollback.TIMESTAMP, sort)
  //          .asObservable()
  //          .unsubscribeOn(RealmSchedulers.getScheduler()))
  //      .flatMap(rollbacks -> database.copyFromRealm(rollbacks))
  //      .subscribeOn(RealmSchedulers.getScheduler())
  //      .observeOn(Schedulers.io());
  //}

  public Observable<Rollback> get(String packageName) {
    return database.get(Rollback.class, Rollback.PACKAGE_NAME, packageName);
  }

  public void save(Rollback rollback) {
    database.insert(rollback);
  }

  public Observable<Rollback> getNotConfirmedRollback(String packageName) {
    return Observable.fromCallable(() -> Database.getInternal())
        .flatMap(realm -> realm.where(Rollback.class)
            .equalTo(Rollback.PACKAGE_NAME, packageName)
            .equalTo(Rollback.CONFIRMED, false)
            .findAllSorted(Rollback.TIMESTAMP, Sort.DESCENDING)
            .asObservable()
            .unsubscribeOn(RealmSchedulers.getScheduler()))
        .flatMap(rollbacks -> database.copyFromRealm(rollbacks))
        .map(rollbacks -> {
          if (rollbacks.size() > 0) {
            return rollbacks.get(0);
          } else {
            return null;
          }
        })
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<List<Rollback>> getConfirmedRollbacks() {
    return Observable.fromCallable(() -> Database.getInternal())
        .flatMap(realm -> realm.where(Rollback.class)
            .equalTo(Rollback.CONFIRMED, true)
            .findAllSorted(Rollback.TIMESTAMP, Sort.DESCENDING)
            .asObservable()
            .unsubscribeOn(RealmSchedulers.getScheduler()))
        .flatMap(rollbacks -> database.copyFromRealm(rollbacks))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }
}
