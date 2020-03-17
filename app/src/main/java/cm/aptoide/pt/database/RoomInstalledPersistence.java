/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.database;

import androidx.annotation.NonNull;
import cm.aptoide.pt.database.accessors.InstallationAccessor;
import cm.aptoide.pt.database.realm.Installation;
import cm.aptoide.pt.database.room.InstalledDao;
import cm.aptoide.pt.database.room.RoomInstalled;
import cm.aptoide.pt.install.InstalledPersistence;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created on 01/09/16.
 */
public class RoomInstalledPersistence implements InstalledPersistence {

  private final InstallationAccessor installationAccessor;
  private final InstalledDao installedDao;

  public RoomInstalledPersistence(InstalledDao installedDao,
      InstallationAccessor installationAccessor) {
    this.installedDao = installedDao;
    this.installationAccessor = installationAccessor;
  }

  public Observable<List<RoomInstalled>> getAllInstalled() {
    return RxJavaInterop.toV1Observable(installedDao.getAll(), BackpressureStrategy.BUFFER)
        .flatMap(installs -> filterCompleted(installs))
        .subscribeOn(Schedulers.io());
  }

  /**
   * @return all the entries from this table even not installed apps
   * if you want only installed apps consider using the install manager
   */
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
    return Completable.fromAction(() -> {
      installedDao.insert(installed);
      installationAccessor.insert(
          new Installation(installed.getPackageName(), installed.getName(), installed.getIcon(),
              installed.getVersionCode(), installed.getVersionName()));
    })
        .subscribeOn(Schedulers.io());
  }

  public Observable<List<RoomInstalled>> getAllAsList(String packageName) {
    return RxJavaInterop.toV1Observable(installedDao.getAsListByPackageName(packageName),
        BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io());
  }

  public Completable clearAndAddAll(List<RoomInstalled> list) {
    return Completable.fromAction(() -> {
      removeAll();
      insertAll(list);
    })
        .subscribeOn(Schedulers.io());
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

  public Observable<List<Installation>> getInstallationsHistory() {
    return installationAccessor.getInstallationsHistory();
  }

  private void insertAll(List<RoomInstalled> installedList) {
    installedDao.insertAll(installedList);
    for (RoomInstalled installed : installedList) {
      installationAccessor.insert(
          new Installation(installed.getPackageName(), installed.getName(), installed.getIcon(),
              installed.getVersionCode(), installed.getVersionName()));
    }
  }

  private void removeAll() {
    installedDao.removeAll();
  }
}
