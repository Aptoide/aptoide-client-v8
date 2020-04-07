package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.AptoideInstallApp;
import cm.aptoide.pt.database.room.RoomInstalled;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import rx.Observable;

public class AptoideInstallAccessor extends SimpleAccessor<AptoideInstallApp> {
  public AptoideInstallAccessor(Database db) {
    super(db, AptoideInstallApp.class);
  }

  public Observable<Boolean> isInstalledWithAptoide(String packageName) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(AptoideInstallApp.class)
            .equalTo(RoomInstalled.PACKAGE_NAME, packageName)
            .findAll()
            .asObservable()
            .unsubscribeOn(RealmSchedulers.getScheduler()))
        .flatMap(database::copyFromRealm)
        .subscribeOn(RealmSchedulers.getScheduler())
        .map(aptoideInstallApps -> {
          if (aptoideInstallApps.isEmpty()) {
            return null;
          } else {
            return aptoideInstallApps.get(0);
          }
        })
        .map(migratedApp -> migratedApp != null);
  }

  public void insert(String packageName) {
    database.insert(new AptoideInstallApp(packageName));
  }
}
