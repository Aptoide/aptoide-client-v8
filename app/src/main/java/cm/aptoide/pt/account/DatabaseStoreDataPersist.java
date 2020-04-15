package cm.aptoide.pt.account;

import cm.aptoide.accountmanager.Store;
import cm.aptoide.pt.database.room.RoomStore;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.store.RoomStoreRepository;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class DatabaseStoreDataPersist {

  private final DatabaseStoreMapper databaseStoreMapper;
  private final RoomStoreRepository storeRepository;

  public DatabaseStoreDataPersist(DatabaseStoreMapper databaseStoreMapper,
      RoomStoreRepository storeRepository) {
    this.storeRepository = storeRepository;
    this.databaseStoreMapper = databaseStoreMapper;
  }

  public Completable persist(List<Store> stores) {
    return Observable.from(stores)
        .map(store -> databaseStoreMapper.toDatabase(store))
        .toList()
        .flatMapCompletable(storeList -> storeRepository.saveAll(storeList))
        .toCompletable();
  }

  public Single<List<Store>> get() {
    return storeRepository.getAll()
        .first()
        .flatMapIterable(list -> list)
        .map(store -> databaseStoreMapper.fromDatabase(store))
        .toList()
        .toSingle()
        .doOnSuccess(stores -> {
          Logger.getInstance()
              .d("DatabaseStoreDataPersist", "nr stores= " + (stores != null ? stores.size() : 0));
        });
  }

  public static class DatabaseStoreMapper {

    public RoomStore toDatabase(Store store) {
      RoomStore result = new RoomStore();
      result.setDownloads(store.getDownloadCount());
      result.setIconPath(store.getAvatar());
      result.setStoreId(store.getId());
      result.setStoreName(store.getName());
      result.setTheme(store.getTheme());
      result.setUsername(store.getUsername());
      result.setPasswordSha1(store.getPassword());
      return result;
    }

    public Store fromDatabase(RoomStore store) {
      return new Store(store.getDownloads(), store.getIconPath(), store.getStoreId(),
          store.getStoreName(), store.getTheme(), store.getUsername(), store.getPasswordSha1(),
          true);
    }
  }
}
