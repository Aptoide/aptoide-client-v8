package cm.aptoide.accountmanager;

import java.util.List;
import rx.Completable;
import rx.Single;

/**
 * concrete persistence abstraction
 */
public interface StoreDataPersist {
  Completable persist(List<Store> stores);

  Single<List<Store>> get();
}
