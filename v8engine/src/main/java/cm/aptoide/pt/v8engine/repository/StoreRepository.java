package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import java.util.List;
import rx.Observable;

/**
 * Created by sithengineer on 11/10/2016.
 */

public class StoreRepository implements Repository<Store, Integer> {
  private final StoreAccessor storeAccessor;

  StoreRepository(StoreAccessor storeAccessor) {
    this.storeAccessor = storeAccessor;
  }

  public Observable<Boolean> isSubscribed(long storeId) {
    return storeAccessor.get(storeId).map(store -> store != null);
  }

  public Observable<Long> count() {
    return storeAccessor.count();
  }

  public Observable<List<Store>> getAll() {
    return storeAccessor.getAll();
  }

  @Override public void save(Store entity) {
    storeAccessor.insert(entity);
  }

  @Override public Observable<Store> get(Integer id) {
    return storeAccessor.get(id);
  }

  public Observable<Store> getByName(String storeName) {
    return storeAccessor.get(storeName);
  }
}
