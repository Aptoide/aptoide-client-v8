package cm.aptoide.pt.install;

import cm.aptoide.pt.database.RoomInstalledPersistence;
import cm.aptoide.pt.database.room.RoomInstallation;
import cm.aptoide.pt.database.room.RoomInstalled;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 7/27/16.
 */
public class InstalledRepository {

  private final RoomInstalledPersistence installedPersistence;

  public InstalledRepository(RoomInstalledPersistence installedPersistence) {
    this.installedPersistence = installedPersistence;
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
   * Get all installed apps
   *
   * @return an observable with a list of installed apps
   */
  public Observable<List<RoomInstalled>> getAllInstalled() {
    return installedPersistence.getAllInstalled();
  }

  public Observable<List<RoomInstalled>> getAllInstalledAndInstalling() {
    return installedPersistence.getAllInstalledAndInstalling();
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

  public Observable<List<RoomInstalled>> getAllInstalledSorted() {
    return installedPersistence.getAllInstalledSorted();
  }

  public Observable<RoomInstalled> get(String packageName, int versionCode) {
    return installedPersistence.get(packageName, versionCode);
  }

  public Observable<List<RoomInstallation>> getInstallationsHistory() {
    return installedPersistence.getInstallationsHistory();
  }
}
