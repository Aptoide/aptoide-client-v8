package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.StoreAccessor;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by sithengineer on 11/10/2016.
 */

@AllArgsConstructor public class StoreRepository implements Repository {
  private final StoreAccessor storeAccessor;

  public Observable<Boolean> isSubscribed(long storeId) {
    return storeAccessor.get(storeId).map(store -> store != null);
  }

  public Observable<Long> count() {
    return storeAccessor.count();
  }
}
