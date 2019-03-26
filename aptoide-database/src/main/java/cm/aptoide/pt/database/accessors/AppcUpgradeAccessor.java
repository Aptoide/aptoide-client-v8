package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.AppcUpgrade;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AppcUpgradeAccessor extends SimpleAccessor<AppcUpgrade> {

  public AppcUpgradeAccessor(Database db) {
    super(db, AppcUpgrade.class);
  }

  public Observable<AppcUpgrade> get(String packageName) {
    return database.get(AppcUpgrade.class, AppcUpgrade.PACKAGE_NAME, packageName);
  }

  public Observable<AppcUpgrade> get(String packageName, boolean isExcluded) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> database.findFirst(realm.where(AppcUpgrade.class)
            .equalTo(AppcUpgrade.PACKAGE_NAME, packageName)
            .equalTo(AppcUpgrade.EXCLUDED, isExcluded)))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<List<AppcUpgrade>> getAll() {
    return database.getAll(AppcUpgrade.class);
  }

  public Observable<List<AppcUpgrade>> getAll(boolean isExcluded) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(AppcUpgrade.class)
            .equalTo(AppcUpgrade.EXCLUDED, isExcluded)
            .findAll()
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<List<AppcUpgrade>> getAllSorted(boolean isExcluded) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(AppcUpgrade.class)
            .equalTo(AppcUpgrade.EXCLUDED, isExcluded)
            .findAllSorted(AppcUpgrade.LABEL)
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<Boolean> contains(String packageName, boolean isExcluded) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> Observable.defer(() -> {
          AppcUpgrade upgrade = realm.where(AppcUpgrade.class)
              .equalTo(AppcUpgrade.EXCLUDED, isExcluded)
              .contains(AppcUpgrade.PACKAGE_NAME, packageName)
              .findFirst();
          return Observable.just(upgrade != null);
        }))
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public void remove(String packageName) {
    database.delete(AppcUpgrade.class, AppcUpgrade.PACKAGE_NAME, packageName);
  }

  public void removeAll(List<String> packageNameList) {
    database.deleteAllIn(AppcUpgrade.class, AppcUpgrade.PACKAGE_NAME,
        packageNameList.toArray(new String[0]));
  }

  public void saveAll(List<AppcUpgrade> upgrades) {
    database.insertAll(upgrades);
  }

  public void save(AppcUpgrade upgrades) {
    database.insert(upgrades);
  }

  public Observable<Boolean> isExcluded(String packageName) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> database.count(realm.where(AppcUpgrade.class)
            .equalTo(AppcUpgrade.PACKAGE_NAME, packageName)
            .equalTo(AppcUpgrade.EXCLUDED, true))
            .map(count -> count > 0L))
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }
}