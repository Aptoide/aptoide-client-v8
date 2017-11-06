package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Store;
import java.util.List;
import rx.Observable;

/**
 * Created by trinkes on 9/2/16.
 */
public class StoreAccessor extends SimpleAccessor<Store> {

  public StoreAccessor(Database db) {
    super(db, Store.class);
  }

  public Observable<List<Store>> getAll() {
    return database.getAll(Store.class);
  }

  public Observable<Store> get(String storeName) {
    return database.get(Store.class, Store.STORE_NAME, storeName);
  }

  public Observable<Store> get(long storeId) {
    return database.get(Store.class, Store.STORE_ID, storeId);
  }

  public void remove(long storeId) {
    database.delete(Store.class, Store.STORE_ID, storeId);
  }

  public void remove(String storeName) {
    database.delete(Store.class, Store.STORE_NAME, storeName);
  }

  public void save(Store store) {
    database.insert(store);
  }

  public Observable<List<Store>> getAsList(long storeId) {
    return database.getAsList(Store.class, Store.STORE_ID, storeId);
  }
}
