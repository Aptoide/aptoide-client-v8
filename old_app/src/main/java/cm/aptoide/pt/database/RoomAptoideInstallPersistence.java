package cm.aptoide.pt.database;

import cm.aptoide.pt.database.room.AptoideInstallDao;
import cm.aptoide.pt.database.room.RoomAptoideInstallApp;
import cm.aptoide.pt.install.AptoideInstallPersistence;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import rx.Single;
import rx.schedulers.Schedulers;

public class RoomAptoideInstallPersistence implements AptoideInstallPersistence {

  private final AptoideInstallDao aptoideInstallDao;

  public RoomAptoideInstallPersistence(AptoideInstallDao aptoideInstallDao) {
    this.aptoideInstallDao = aptoideInstallDao;
  }

  @Override public Single<Boolean> isInstalledWithAptoide(String packageName) {
    return RxJavaInterop.toV1Single(aptoideInstallDao.get(packageName))
        .map(installedApp -> true)
        .onErrorReturn(throwable -> false)
        .subscribeOn(Schedulers.io());
  }

  @Override public void insert(String packageName) {
    new Thread(() -> aptoideInstallDao.insert(new RoomAptoideInstallApp(packageName))).start();
  }
}
