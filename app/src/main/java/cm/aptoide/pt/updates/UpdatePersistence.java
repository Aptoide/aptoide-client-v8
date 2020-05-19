package cm.aptoide.pt.updates;

import cm.aptoide.pt.database.room.RoomUpdate;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public interface UpdatePersistence {

  Single<RoomUpdate> get(String packageName);

  Single<List<RoomUpdate>> getAll(boolean isExcluded);

  Observable<List<RoomUpdate>> getAllSorted(boolean isExcluded);

  Single<Boolean> contains(String packageName, boolean isExcluded);

  Single<Boolean> contains(String packageName, boolean isExcluded, boolean isAppcUpgrade);

  Completable remove(String packageName);

  Completable removeAll(List<RoomUpdate> roomUpdatesList);

  Completable saveAll(List<RoomUpdate> updates);

  Completable save(RoomUpdate update);

  Single<Boolean> isExcluded(String packageName);
}
