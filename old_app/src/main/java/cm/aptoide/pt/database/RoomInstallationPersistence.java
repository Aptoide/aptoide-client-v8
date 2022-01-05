package cm.aptoide.pt.database;

import cm.aptoide.pt.database.room.InstallationDao;
import cm.aptoide.pt.database.room.RoomInstallation;
import cm.aptoide.pt.install.InstallationPersistence;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RoomInstallationPersistence implements InstallationPersistence {

  private final InstallationDao installationDao;

  public RoomInstallationPersistence(InstallationDao installationDao) {
    this.installationDao = installationDao;
  }

  public Observable<List<RoomInstallation>> getInstallationsHistory() {
    return RxJavaInterop.toV1Observable(installationDao.getAll(), BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io());
  }

  public Completable insertAll(List<RoomInstallation> roomInstallationList) {
    return Completable.fromAction(() -> installationDao.insertAll(roomInstallationList))
        .subscribeOn(Schedulers.io());
  }

  public Completable insert(RoomInstallation roomInstallation) {
    return Completable.fromAction(() -> installationDao.insert(roomInstallation))
        .subscribeOn(Schedulers.io());
  }
}
