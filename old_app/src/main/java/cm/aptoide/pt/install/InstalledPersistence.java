package cm.aptoide.pt.install;

import cm.aptoide.pt.database.room.RoomInstalled;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public interface InstalledPersistence {

  Observable<List<RoomInstalled>> getAllInstalled();

  /**
   * @return all the entries from this table even not installed apps
   * if you want only installed apps consider using the install manager
   */
  Observable<List<RoomInstalled>> getAll();

  Observable<List<RoomInstalled>> getAllInstalledSorted();

  Completable remove(String packageName, int versionCode);

  Observable<Boolean> isInstalled(String packageName);

  Observable<RoomInstalled> getInstalled(String packageName);

  Observable<RoomInstalled> get(String packageName, int versionCode);

  Observable<List<RoomInstalled>> getAsList(String packageName, int versionCode);

  Completable insert(RoomInstalled installed);

  Observable<List<RoomInstalled>> getAllAsList(String packageName);

  Completable replaceAllBy(List<RoomInstalled> list);

  Observable<List<RoomInstalled>> getAllInstalling();

  Single<Boolean> isInstalled(String packageName, int versionCode);

  Observable<List<RoomInstalled>> getInstalledFilteringSystemApps();
}
