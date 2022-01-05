package cm.aptoide.pt.store;

import cm.aptoide.pt.database.room.RoomStore;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public interface StorePersistence {
  Observable<List<RoomStore>> getAll();

  Single<RoomStore> get(String storeName);

  Single<RoomStore> get(long storeId);

  Completable remove(String storeName);

  Completable save(RoomStore store);

  Observable<Boolean> isSubscribed(long storeId);

  Completable saveAll(List<RoomStore> stores);
}
