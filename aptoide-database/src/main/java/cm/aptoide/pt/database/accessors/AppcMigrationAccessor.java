package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.MigratedApp;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import rx.Observable;

public class AppcMigrationAccessor extends SimpleAccessor<MigratedApp> {

  public AppcMigrationAccessor(Database db) {
    super(db, MigratedApp.class);
  }

  public Observable<Boolean> isAppMigrated(String packageName) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(MigratedApp.class)
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
    database.insert(new MigratedApp(packageName));
  }
}
