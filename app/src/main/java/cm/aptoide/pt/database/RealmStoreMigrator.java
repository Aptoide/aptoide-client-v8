package cm.aptoide.pt.database;

import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.room.RoomStore;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.store.StorePersistence;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;

public class RealmStoreMigrator {

  private final StoreRepository storeRepository;
  private StorePersistence storePersistence;

  public RealmStoreMigrator(StorePersistence storePersistence, StoreRepository storeRepository) {
    this.storePersistence = storePersistence;
    this.storeRepository = storeRepository;
  }

  public Completable performMigration() {
    return storeRepository.getAll()
        .map(realmList -> mapRealmStoreListToRoomStoreList(realmList))
        .flatMapCompletable(roomList -> storePersistence.saveAll(roomList))
        .toCompletable();
  }

  private List<RoomStore> mapRealmStoreListToRoomStoreList(List<Store> realmList) {
    ArrayList<RoomStore> roomStoreList = new ArrayList<>();
    for (Store store : realmList) {
      RoomStore roomStore = new RoomStore(store.getStoreId(), store.getIconPath(), store.getTheme(),
          store.getDownloads(), store.getStoreName(), store.getUsername(), store.getPasswordSha1());
      roomStoreList.add(roomStore);
    }
    return roomStoreList;
  }
}
