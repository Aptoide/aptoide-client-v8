package cm.aptoide.pt.store;

import cm.aptoide.pt.database.room.RoomStore;
import java.util.List;
import rx.Completable;
import rx.Observable;

public interface StorePersistence {
  Observable<List<RoomStore>> getAll();

  Observable<RoomStore> get(String storeName);

  Observable<RoomStore> get(long storeId);

  Completable remove(String storeName);

  Completable save(RoomStore store);

  Observable<Boolean> isSubscribed(long storeId);

  Observable<Long> countAll();
}
