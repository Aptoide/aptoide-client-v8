package cm.aptoide.pt.database;

import android.content.SharedPreferences;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.room.RoomStore;
import cm.aptoide.pt.store.StorePersistence;
import cm.aptoide.pt.store.StoreRepository;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;

public class RealmStoreMigrator {

  private static final String MIGRATION_KEY = "realmMigration";
  private final StoreRepository storeRepository;
  private StorePersistence storePersistence;
  private SharedPreferences sharedPreferences;

  public RealmStoreMigrator(StorePersistence storePersistence, StoreRepository storeRepository,
      SharedPreferences sharedPreferences) {
    this.storePersistence = storePersistence;
    this.storeRepository = storeRepository;
    this.sharedPreferences = sharedPreferences;
  }

  public Completable performMigration() {
    if (hasDoneMigration()) {
      return Completable.complete();
    } else {
      return storeRepository.getAll()
          .map(realmList -> mapRealmStoreListToRoomStoreList(realmList))
          .flatMapCompletable(roomList -> storePersistence.saveAll(roomList)
              .andThen(saveMig()))
          .doOnError(throwable -> throwable.printStackTrace())
          .toCompletable();
    }
  }

  public Completable saveMig() {
    return Completable.fromAction(() -> saveMigrationFlag());
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

  private boolean hasDoneMigration() {
    return sharedPreferences.getBoolean(MIGRATION_KEY, false);
  }

  private void saveMigrationFlag() {
    sharedPreferences.edit()
        .putBoolean(MIGRATION_KEY, true)
        .apply();
  }
}
