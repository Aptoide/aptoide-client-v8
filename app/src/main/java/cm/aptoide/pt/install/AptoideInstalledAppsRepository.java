package cm.aptoide.pt.install;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cm.aptoide.pt.database.RoomInstalledPersistence;
import cm.aptoide.pt.database.room.RoomInstallation;
import cm.aptoide.pt.database.room.RoomInstalled;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 7/27/16.
 */
public class AptoideInstalledAppsRepository implements InstalledAppsRepository {

  private final RoomInstalledPersistence installedPersistence;
  private final PackageManager packageManager;
  private final FileUtils fileUtils;
  private boolean synced = false;

  public AptoideInstalledAppsRepository(RoomInstalledPersistence installedPersistence,
      PackageManager packageManager, FileUtils fileUtils) {
    this.installedPersistence = installedPersistence;
    this.packageManager = packageManager;
    this.fileUtils = fileUtils;
  }

  public Completable syncWithDevice(String packageName) {
    return Observable.fromCallable(() -> {
          PackageInfo appPackageInfo = AptoideUtils.SystemU.getPackageInfo(packageName, packageManager);
          return appPackageInfo;
        }).onErrorReturn(null)
        .flatMapCompletable(
            searchedApp -> {
              if (searchedApp != null) {
                return installedPersistence.isInstalled(packageName, searchedApp.versionCode)
                    .flatMapCompletable(isInstalled -> {
                      if (!isInstalled) {
                        return installedPersistence.insert(
                            new RoomInstalled(searchedApp, packageManager, fileUtils));
                      }
                      return Completable.complete();
                    });
              } else {
                return installedPersistence.remove(packageName);
              }
            }
        ).doOnError(throwable -> throwable.printStackTrace())
        .toCompletable();
  }

  public Completable syncWithDevice() {
    return Observable.fromCallable(() -> {
          // get the installed apps
          List<PackageInfo> installedApps = AptoideUtils.SystemU.getAllInstalledApps(packageManager);
          Logger.getInstance()
              .v("InstalledRepository", "Found " + installedApps.size() + " user installed apps.");

          // Installed apps are inserted in database based on their firstInstallTime. Older comes first.
          Collections.sort(installedApps,
              (lhs, rhs) -> (int) ((lhs.firstInstallTime - rhs.firstInstallTime) / 1000));

          // return sorted installed apps
          return installedApps;
        })  // transform installation package into Installed table entry and save all the data
        .flatMapIterable(list -> list)
        .map(packageInfo -> new RoomInstalled(packageInfo, packageManager, fileUtils))
        .toList()
        .flatMap(appsInstalled -> installedPersistence.getAll()
            .first()
            .map(installedFromDatabase -> combineLists(appsInstalled, installedFromDatabase,
                installed -> installed.setStatus(RoomInstalled.STATUS_UNINSTALLED))))
        .flatMapCompletable(installedPersistence::replaceAllBy)
        .toCompletable()
        .doOnCompleted(() -> synced = true);
  }

  private <T> List<T> combineLists(List<T> list1, List<T> list2, @Nullable Action1<T> transformer) {
    List<T> toReturn = new ArrayList<>(list1.size() + list2.size());
    toReturn.addAll(list1);
    for (T item : list2) {
      if (!toReturn.contains(item)) {
        if (transformer != null) {
          transformer.call(item);
        }
        toReturn.add(item);
      }
    }

    return toReturn;
  }

  public Completable save(RoomInstalled installed) {
    return installedPersistence.insert(installed);
  }

  public boolean contains(String packageName) {
    return installedPersistence.isInstalled(packageName)
        .toBlocking()
        .first();
  }

  /**
   * This method assures that it returns a list of installed apps synced with the the device. If it
   * hasn't been synced yet, sync it before returning. Note that it only assures that these apps
   * were synced at least once since the app started.
   */
  public Single<List<RoomInstalled>> getAllSyncedInstalled() {
    if (!synced) {
      return syncWithDevice().andThen(getAllInstalled().first()
          .toSingle());
    }
    return getAllInstalled().first()
        .toSingle();
  }

  public Observable<List<RoomInstalled>> getAllInstalled() {
    return installedPersistence.getAllInstalled();
  }

  public Observable<RoomInstalled> getAsList(String packageName, int versionCode) {
    return installedPersistence.getAsList(packageName, versionCode)
        .observeOn(Schedulers.io())
        .map(installedList -> {
          if (installedList.isEmpty()) {
            return null;
          } else {
            return installedList.get(0);
          }
        });
  }

  public Observable<List<RoomInstalled>> getAsList(String packageName) {
    return installedPersistence.getAllAsList(packageName);
  }

  public Observable<RoomInstalled> getInstalled(String packageName) {
    return installedPersistence.getInstalled(packageName);
  }

  public Completable remove(String packageName, int versionCode) {
    return installedPersistence.remove(packageName, versionCode);
  }

  public Observable<Boolean> isInstalled(String packageName) {
    return installedPersistence.isInstalled(packageName);
  }

  public Single<Boolean> isInstalled(String packageName, int versionCode) {
    return installedPersistence.isInstalled(packageName, versionCode);
  }

  public Observable<List<RoomInstalled>> getAllInstalledSorted() {
    return installedPersistence.getAllInstalledSorted();
  }

  public Observable<RoomInstalled> get(String packageName, int versionCode) {
    return installedPersistence.get(packageName, versionCode);
  }

  public Observable<List<RoomInstallation>> getInstallationsHistory() {
    return installedPersistence.getInstallationsHistory();
  }

  public Observable<List<RoomInstalled>> getAllInstalling() {
    return installedPersistence.getAllInstalling();
  }

  public Observable<List<RoomInstalled>> getInstalledAppsFilterSystem() {
    return installedPersistence.getInstalledFilteringSystemApps();
  }

  @NonNull @Override public io.reactivex.Single<List<String>> getInstalledAppsNames() {
    return RxJavaInterop.toV2Single(getAllInstalled().first()
        .flatMapIterable(roomInstalleds -> roomInstalleds)
        .map(RoomInstalled::getPackageName)
        .toList()
        .toSingle());
  }
}
