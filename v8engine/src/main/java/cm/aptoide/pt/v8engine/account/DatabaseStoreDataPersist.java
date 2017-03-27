package cm.aptoide.pt.v8engine.account;

import cm.aptoide.accountmanager.Store;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.logger.Logger;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class DatabaseStoreDataPersist {

  private final StoreAccessor accessor;
  private final DatabaseStoreMapper databaseStoreMapper;

  public DatabaseStoreDataPersist(StoreAccessor accessor, DatabaseStoreMapper databaseStoreMapper) {
    this.accessor = accessor;
    this.databaseStoreMapper = databaseStoreMapper;
  }

  public Completable persist(List<Store> stores) {
    return Observable.from(stores)
        .map(store -> databaseStoreMapper.toDatabase(store))
        .toList()
        .doOnNext(storeList -> accessor.insertAll(storeList))
        .toCompletable();
  }

  public Single<List<Store>> get() {
    return accessor.getAll()
        .first()
        .flatMapIterable(list -> list)
        .map(store -> databaseStoreMapper.fromDatabase(store))
        .toList()
        .toSingle()
        .doOnSuccess(stores -> {
          Logger.d("DatabaseStoreDataPersist", "nr stores= " + (stores != null ? stores.size() : 0));
        });
  }

  public static class DatabaseStoreMapper {

    public cm.aptoide.pt.database.realm.Store toDatabase(Store store) {
      cm.aptoide.pt.database.realm.Store result = new cm.aptoide.pt.database.realm.Store();
      result.setDownloads(store.getDownloadCount());
      result.setIconPath(store.getAvatar());
      result.setStoreId(store.getId());
      result.setStoreName(store.getName());
      result.setTheme(store.getTheme());
      result.setUsername(store.getUsername());
      result.setPasswordSha1(store.getPassword());
      return result;
    }

    public Store fromDatabase(cm.aptoide.pt.database.realm.Store store) {
      return new Store(store.getDownloads(), store.getIconPath(), store.getStoreId(),
          store.getStoreName(), store.getTheme(), store.getUsername(), store.getPasswordSha1());
    }
  }
}
