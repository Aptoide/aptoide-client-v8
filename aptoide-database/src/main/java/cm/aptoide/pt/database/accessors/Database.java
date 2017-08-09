package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import java.util.List;
import lombok.Cleanup;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created on 16/05/16.
 *
 * This is the main class responsible to offer {@link Realm} database instances
 */
public final class Database {

  public Realm get() {
    return Realm.getDefaultInstance();
  }

  public <E extends RealmObject> Observable<Long> count(RealmQuery<E> query) {
    return Observable.just(query.count())
        .flatMap(count -> Observable.just(count)
            .unsubscribeOn(RealmSchedulers.getScheduler()))
        .defaultIfEmpty(0L);
  }

  public <E extends RealmObject> Observable<List<E>> copyFromRealm(RealmResults<E> results) {
    return Observable.just(results)
        .filter(data -> data.isLoaded())
        .map(realmObjects -> get().copyFromRealm(realmObjects))
        .observeOn(Schedulers.io());
  }

  public Observable<Long> count(Class clazz) {
    return getRealm().flatMap(realm -> Observable.just(realm.where(clazz)
        .count())
        .unsubscribeOn(RealmSchedulers.getScheduler()));
  }

  public Observable<Realm> getRealm() {
    return Observable.just(null)
        .observeOn(RealmSchedulers.getScheduler())
        .map(something -> get());
  }

  public <E extends RealmObject> Observable<List<E>> getAll(Class<E> clazz) {
    return getRealm().flatMap(realm -> realm.where(clazz)
        .findAll().<List<E>>asObservable().unsubscribeOn(RealmSchedulers.getScheduler()))
        .flatMap(results -> copyFromRealm(results));
  }

  public <E extends RealmObject> Observable<E> get(Class<E> clazz, String key, String value) {
    return getRealm().map(realm -> realm.where(clazz)
        .equalTo(key, value))
        .flatMap(query -> findFirst(query));
  }

  public <E extends RealmObject> Observable<E> findFirst(RealmQuery<E> query) {
    return Observable.just(query.findFirst())
        .filter(realmObject -> realmObject != null)
        .flatMap(realmObject -> realmObject.<E>asObservable().unsubscribeOn(
            RealmSchedulers.getScheduler()))
        .flatMap(realmObject -> copyFromRealm(realmObject))
        .defaultIfEmpty(null);
  }

  private <E extends RealmObject> Observable<E> copyFromRealm(E object) {
    return Observable.just(object)
        .filter(data -> data.isLoaded())
        .map(realmObject -> get().copyFromRealm(realmObject))
        .observeOn(Schedulers.io());
  }

  public <E extends RealmObject> Observable<E> get(Class<E> clazz, String key, Integer value) {
    return getRealm().map(realm -> realm.where(clazz)
        .equalTo(key, value))
        .flatMap(query -> findFirst(query));
  }

  public <E extends RealmObject> Observable<E> get(Class<E> clazz, String key, Long value) {
    return getRealm().map(realm -> realm.where(clazz)
        .equalTo(key, value))
        .flatMap(query -> findFirst(query));
  }

  public <E extends RealmObject> Observable<List<E>> getAsList(Class<E> clazz, String key,
      String value) {
    return getRealm().map(realm -> realm.where(clazz)
        .equalTo(key, value))
        .flatMap(query -> findAsList(query));
  }

  public <E extends RealmObject> Observable<List<E>> findAsList(RealmQuery<E> query) {
    return Observable.just(query.findAll())
        .filter(realmObject -> realmObject != null)
        .flatMap(realmObject -> realmObject.<E>asObservable().unsubscribeOn(
            RealmSchedulers.getScheduler()))
        .flatMap(realmObject -> copyFromRealm(realmObject))
        .defaultIfEmpty(null);
  }

  public <E extends RealmObject> Observable<List<E>> getAsList(Class<E> clazz, String key,
      Long value) {
    return getRealm().map(realm -> realm.where(clazz)
        .equalTo(key, value))
        .flatMap(query -> findAsList(query));
  }

  public <E extends RealmObject> void delete(Class<E> clazz, String key, String value) {
    @Cleanup Realm realm = get();
    E obj = realm.where(clazz)
        .equalTo(key, value)
        .findFirst();
    deleteObject(realm, obj);
  }

  public <E extends RealmObject> void deleteObject(Realm realm, E obj) {
    realm.beginTransaction();
    try {
      if (obj != null && obj.isValid()) {
        obj.deleteFromRealm();
        realm.commitTransaction();
      } else {
        realm.cancelTransaction();
      }
    } catch (Exception ex) {
      CrashReport.getInstance()
          .log(ex);
      realm.cancelTransaction();
    }
  }

  public <E extends RealmObject> void delete(Class<E> clazz, String key, Integer value) {
    @Cleanup Realm realm = get();
    E obj = realm.where(clazz)
        .equalTo(key, value)
        .findFirst();
    deleteObject(realm, obj);
  }

  public <E extends RealmObject> void delete(Class<E> clazz, String key, Long value) {
    @Cleanup Realm realm = get();
    E obj = realm.where(clazz)
        .equalTo(key, value)
        .findFirst();
    deleteObject(realm, obj);
  }

  public <E extends RealmObject> void deleteAll(Class<E> clazz) {
    @Cleanup Realm realm = get();
    realm.beginTransaction();
    realm.delete(clazz);
    realm.commitTransaction();
  }

  public <E extends RealmObject> void insertAll(List<E> objects) {
    @Cleanup Realm realm = get();
    realm.beginTransaction();
    realm.insertOrUpdate(objects);
    realm.commitTransaction();
  }

  public <E extends RealmObject> void insert(E object) {
    @Cleanup Realm realm = get();
    realm.beginTransaction();
    realm.insertOrUpdate(object);
    realm.commitTransaction();
  }

  public <E extends RealmObject> void deleteAllIn(Class<E> classType, String classField,
      String[] fieldsIn) {
    @Cleanup Realm realm = get();
    realm.beginTransaction();
    realm.where(classType)
        .in(classField, fieldsIn)
        .findAll()
        .deleteAllFromRealm();
    realm.commitTransaction();
  }

  public <E extends RealmObject> void deleteAllExcluding(Class<E> classType, String classField,
      List<String> fieldsIn) {
    @Cleanup Realm realm = get();
    realm.beginTransaction();
    RealmQuery<E> query = realm.where(classType);
    for (String field : fieldsIn) {
      query.notEqualTo(classField, field);
    }
    query.findAll()
        .deleteAllFromRealm();
    realm.commitTransaction();
  }
}
