package cm.aptoide.pt.database;

import cm.aptoide.pt.app.migration.AppcMigrationPersistence;
import cm.aptoide.pt.database.room.MigratedAppDAO;
import cm.aptoide.pt.database.room.RoomMigratedApp;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RoomAppcMigrationPersistence implements AppcMigrationPersistence {

  private MigratedAppDAO migratedAppDAO;

  public RoomAppcMigrationPersistence(MigratedAppDAO migratedAppDAO) {
    this.migratedAppDAO = migratedAppDAO;
  }

  public Observable<Boolean> isAppMigrated(String packageName) {
    return RxJavaInterop.toV1Observable(migratedAppDAO.isAppMigrated(packageName),
        BackpressureStrategy.BUFFER)
        .map(count -> count > 0)
        .subscribeOn(Schedulers.io());
  }

  public void insert(String packageName) {
    new Thread(() -> migratedAppDAO.save(new RoomMigratedApp(packageName))).start();
  }
}
