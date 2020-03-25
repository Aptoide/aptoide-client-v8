package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.room.RoomMigratedApp;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import rx.Observable;

public class AppcMigrationPersistence extends SimpleAccessor<RoomMigratedApp> {

  public AppcMigrationPersistence(Database db) {
    super(db, RoomMigratedApp.class);
  }

  public Observable<Boolean> isAppMigrated(String packageName) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(RoomMigratedApp.class)
            .equalTo(Installed.PACKAGE_NAME, packageName)
            .findAll()
            .asObservable()
            .unsubscribeOn(RealmSchedulers.getScheduler()))
        .flatMap(database::copyFromRealm)
        .subscribeOn(RealmSchedulers.getScheduler())
        .map(migratedApps -> {
          if (migratedApps.isEmpty()) {
            return null;
          } else {
            return migratedApps.get(0);
          }
        })
        .map(migratedApp -> migratedApp != null);
  }

  public void insert(String packageName) {
    database.insert(new RoomMigratedApp(packageName));
  }
}
