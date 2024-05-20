package cm.aptoide.pt.database;

import androidx.annotation.NonNull;
import cm.aptoide.pt.database.room.InstalledDao;
import cm.aptoide.pt.database.room.RoomInstallation;
import cm.aptoide.pt.database.room.RoomInstalled;
import cm.aptoide.pt.install.InstallationPersistence;
import cm.aptoide.pt.install.InstalledPersistence;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class RoomInstalledPersistence implements InstalledPersistence {

  private final InstallationPersistence roomInstallationPersistence;
  private final InstalledDao installedDao;
  private final RoomInstallationMapper installationMapper;

  public RoomInstalledPersistence(InstalledDao installedDao,
      RoomInstallationPersistence roomInstallationPersistence,
      RoomInstallationMapper installationMapper) {
    this.installedDao = installedDao;
    this.roomInstallationPersistence = roomInstallationPersistence;
    this.installationMapper = installationMapper;
  }

  public Observable<List<RoomInstalled>> getAllInstalled() {
    return RxJavaInterop.toV1Observable(installedDao.getAll(), BackpressureStrategy.BUFFER)
        .flatMap(installs -> filterCompleted(installs))
        .subscribeOn(Schedulers.io());
  }

  public Observable<List<RoomInstalled>> getAll() {
    return RxJavaInterop.toV1Observable(installedDao.getAll(), BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io());
  }

  public Observable<List<RoomInstalled>> getAllInstalledSorted() {
    return RxJavaInterop.toV1Observable(installedDao.getAllSortedAsc(), BackpressureStrategy.BUFFER)
        .flatMap(installs -> filterCompleted(installs))
        .subscribeOn(Schedulers.io());
  }

  public Completable remove(String packageName, int versionCode) {
    return RxJavaInterop.toV1Completable(installedDao.remove(packageName, versionCode))
        .subscribeOn(Schedulers.io());
  }

  public Completable remove(String packageName) {
    return RxJavaInterop.toV1Completable(installedDao.remove(packageName))
        .subscribeOn(Schedulers.io());
  }

  public Observable<Boolean> isInstalled(String packageName) {
    return getInstalled(packageName).map(
        installed -> installed != null && installed.getStatus() == RoomInstalled.STATUS_COMPLETED);
  }

  public Observable<RoomInstalled> getInstalled(String packageName) {
    return getInstalledAsList(packageName).map(installedList -> {
      if (installedList.isEmpty()) {
        return null;
      } else {
        return installedList.get(0);
      }
    });
  }

  public Observable<RoomInstalled> get(String packageName, int versionCode) {
    return RxJavaInterop.toV1Observable(installedDao.get(packageName, versionCode),
            BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io());
  }

  public Observable<List<RoomInstalled>> getAsList(String packageName, int versionCode) {
    return RxJavaInterop.toV1Observable(installedDao.getAsList(packageName, versionCode),
            BackpressureStrategy.BUFFER)
        .onErrorReturn(throwable -> new ArrayList<>())
        .subscribeOn(Schedulers.io());
  }

  public Completable insert(RoomInstalled installed) {
    return Completable.fromAction(() -> installedDao.insert(installed))
        .andThen(roomInstallationPersistence.insert(installationMapper.map(installed)))
        .subscribeOn(Schedulers.io());
  }

  public Observable<List<RoomInstalled>> getAllAsList(String packageName) {
    return RxJavaInterop.toV1Observable(installedDao.getAsListByPackageName(packageName),
            BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io());
  }

  public Completable replaceAllBy(List<RoomInstalled> list) {
    return Completable.fromAction(() -> {
          installedDao.removeAll();
          installedDao.insertAll(list);
        })
        .andThen(roomInstallationPersistence.insertAll(installationMapper.map(list)))
        .subscribeOn(Schedulers.io());
  }

  @Override public Observable<List<RoomInstalled>> getAllInstalling() {
    return RxJavaInterop.toV1Observable(installedDao.getAll(), BackpressureStrategy.BUFFER)
        .flatMap(installs -> filterInstalling(installs))
        .subscribeOn(Schedulers.io());
  }

  @Override public Single<Boolean> isInstalled(String packageName, int versionCode) {
    return RxJavaInterop.toV1Single(installedDao.isInstalledByVersion(packageName, versionCode))
        .onErrorReturn(throwable -> null)
        .map(installed -> installed != null
            && installed.getStatus() == RoomInstalled.STATUS_COMPLETED
        )
        .subscribeOn(Schedulers.io());
  }

  @Override public Observable<List<RoomInstalled>> getInstalledFilteringSystemApps() {
    return RxJavaInterop.toV1Observable(installedDao.getAllFilteringSystemApps(),
            BackpressureStrategy.BUFFER)
        .flatMap(installs -> filterCompleted(installs))
        .subscribeOn(Schedulers.io());
  }

  private Observable<List<RoomInstalled>> filterInstalling(List<RoomInstalled> installs) {
    return Observable.from(installs)
        .filter(installed -> installed.getStatus() == RoomInstalled.STATUS_INSTALLING)
        .toList();
  }

  @NonNull private Observable<List<RoomInstalled>> filterCompleted(List<RoomInstalled> installs) {
    return Observable.from(installs)
        .filter(installed -> installed.getStatus() == RoomInstalled.STATUS_COMPLETED)
        .toList();
  }

  private Observable<List<RoomInstalled>> getInstalledAsList(String packageName) {
    return RxJavaInterop.toV1Observable(installedDao.getAsListByPackageName(packageName),
            BackpressureStrategy.BUFFER)
        .onErrorReturn(throwable -> new ArrayList<>())
        .flatMap(installs -> filterCompleted(installs))
        .subscribeOn(Schedulers.io());
  }

  public Observable<List<RoomInstallation>> getInstallationsHistory() {
    return roomInstallationPersistence.getInstallationsHistory();
  }
}
