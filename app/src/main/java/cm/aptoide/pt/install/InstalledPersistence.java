package cm.aptoide.pt.install;

import cm.aptoide.pt.database.room.RoomInstalled;
import java.util.List;
import rx.Completable;
import rx.Observable;

public interface InstalledPersistence {

  Observable<List<RoomInstalled>> getAllInstalled();

  Observable<List<RoomInstalled>> getAll();

  Observable<List<RoomInstalled>> getAllInstalledSorted();

  Completable remove(String packageName, int versionCode);

  Observable<Boolean> isInstalled(String packageName);

  Observable<RoomInstalled> getInstalled(String packageName);

  Observable<RoomInstalled> get(String packageName, int versionCode);

  Observable<List<RoomInstalled>> getAsList(String packageName, int versionCode);

  Completable insert(RoomInstalled installed);

  Observable<List<RoomInstalled>> getAllAsList(String packageName);

  Completable clearAndAddAll(List<RoomInstalled> list);
}
