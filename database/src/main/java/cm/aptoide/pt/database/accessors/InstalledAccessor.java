/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import io.realm.Sort;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created on 01/09/16.
 */
public class InstalledAccessor extends SimpleAccessor<Installed> {

  public InstalledAccessor(Database db) {
    super(db, Installed.class);
  }

  public Observable<List<Installed>> getAll() {
    return database.getAll(Installed.class);
  }

  public Observable<List<Installed>> getAllSorted() {
    return getAllSorted(Sort.ASCENDING);
  }

  public Observable<List<Installed>> getAllSorted(Sort sort) {
    return Observable.fromCallable(() -> Database.getInternal())
        .flatMap(realm -> realm.where(Installed.class)
            .findAllSorted(Installed.NAME, sort)
            .asObservable()
            .unsubscribeOn(RealmSchedulers.getScheduler()))
        .flatMap(installed -> database.copyFromRealm(installed))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public void remove(String packageName) {
    database.delete(Installed.class, Installed.PACKAGE_NAME, packageName);
  }

  public Observable<Boolean> isInstalled(String packageName) {
    return get(packageName).map(installed -> installed != null);
  }

  public Observable<Installed> get(String packageName) {
    return database.get(Installed.class, Installed.PACKAGE_NAME, packageName);
  }

  public Observable<List<Installed>> getAsList(String packageName) {
    return Observable.fromCallable(() -> Database.getInternal())
        .flatMap(realm -> realm.where(Installed.class)
            .equalTo(Installed.PACKAGE_NAME, packageName)
            .findAll()
            .asObservable()
            .unsubscribeOn(RealmSchedulers.getScheduler()))
        .flatMap(installeds -> database.copyFromRealm(installeds))
        .subscribeOn(RealmSchedulers.getScheduler());
  }

  public Observable<List<Installed>> get(String[] apps) {
    return Observable.fromCallable(() -> Database.getInternal())
        .flatMap(realm -> realm.where(Installed.class)
            .in(Installed.PACKAGE_NAME, apps)
            .findAll()
            .asObservable()
            .unsubscribeOn(RealmSchedulers.getScheduler()))
        .flatMap(installeds -> database.copyFromRealm(installeds))
        .subscribeOn(RealmSchedulers.getScheduler());
  }

  public void insertAll(List<Installed> installedList) {
    database.insertAll(installedList);
  }

  public void insert(Installed installed) {
    database.insert(installed);
  }
}
