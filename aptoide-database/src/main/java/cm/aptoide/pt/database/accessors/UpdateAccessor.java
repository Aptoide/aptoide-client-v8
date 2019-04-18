package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 9/2/16.
 */
public class UpdateAccessor extends SimpleAccessor<Update> {

  public UpdateAccessor(Database db) {
    super(db, Update.class);
  }

  public Observable<Update> get(String packageName) {
    return database.get(Update.class, Update.PACKAGE_NAME, packageName);
  }

  public Observable<Update> get(String packageName, boolean isExcluded) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> database.findFirst(realm.where(Update.class)
            .equalTo(Update.PACKAGE_NAME, packageName)
            .equalTo(Update.EXCLUDED, isExcluded)))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<List<Update>> getAll() {
    return database.getAll(Update.class);
  }

  public Observable<List<Update>> getAll(boolean isExcluded) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(Update.class)
            .equalTo(Update.EXCLUDED, isExcluded)
            .findAll()
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<List<Update>> getAllSorted(boolean isExcluded) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(Update.class)
            .equalTo(Update.EXCLUDED, isExcluded)
            .findAllSorted(Update.LABEL)
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<Boolean> contains(String packageName, boolean isExcluded) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> Observable.defer(() -> {
          Update update = realm.where(Update.class)
              .equalTo(Update.EXCLUDED, isExcluded)
              .contains(Update.PACKAGE_NAME, packageName)
              .findFirst();
          return Observable.just(update != null);
        }))
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<Boolean> contains(String packageName, boolean isExcluded,
      boolean isAppcUpgrade) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> Observable.defer(() -> {
          Update update = realm.where(Update.class)
              .equalTo(Update.EXCLUDED, isExcluded)
              .equalTo(Update.APPC_UPGRADE, isAppcUpgrade)
              .contains(Update.PACKAGE_NAME, packageName)
              .findFirst();
          return Observable.just(update != null);
        }))
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public void remove(String packageName) {
    database.delete(Update.class, Update.PACKAGE_NAME, packageName);
  }

  public void removeAll(List<String> packageNameList) {
    database.deleteAllIn(Update.class, Update.PACKAGE_NAME, packageNameList.toArray(new String[0]));
  }

  public void saveAll(List<Update> updates) {
    database.insertAll(updates);
  }

  public void save(Update update) {
    database.insert(update);
  }

  public Observable<Boolean> isExcluded(String packageName) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> database.count(realm.where(Update.class)
            .equalTo(Update.PACKAGE_NAME, packageName)
            .equalTo(Update.EXCLUDED, true))
            .map(count -> count > 0L))
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }
}
