package cm.aptoide.pt.store;

import cm.aptoide.pt.database.room.RoomStore;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class RoomStoreRepository {

  private final StorePersistence storePersistence;

  public RoomStoreRepository(StorePersistence storePersistence) {
    this.storePersistence = storePersistence;
  }

  public Observable<Boolean> isSubscribed(long storeId) {
    return storePersistence.isSubscribed(storeId);
  }

  public Observable<List<RoomStore>> getAll() {
    return storePersistence.getAll();
  }

  public Completable save(RoomStore entity) {
    return storePersistence.save(entity);
  }

  public Single<RoomStore> get(Long id) {
    return storePersistence.get(id);
  }

  public Single<RoomStore> get(String storeName) {
    return storePersistence.get(storeName);
  }

  public Completable saveAll(List<RoomStore> stores) {
    return storePersistence.saveAll(stores);
  }

  public Completable remove(String storeName) {
    return storePersistence.remove(storeName);
  }
}
