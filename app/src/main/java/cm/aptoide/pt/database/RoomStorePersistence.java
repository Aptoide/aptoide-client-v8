package cm.aptoide.pt.database;

import cm.aptoide.pt.database.room.RoomStore;
import cm.aptoide.pt.database.room.StoreDao;
import cm.aptoide.pt.store.StorePersistence;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class RoomStorePersistence implements StorePersistence {

  private final StoreDao storeDao;

  public RoomStorePersistence(StoreDao storeDao) {
    this.storeDao = storeDao;
  }

  public Observable<List<RoomStore>> getAll() {
    return RxJavaInterop.toV1Observable(storeDao.getAll(), BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io());
  }

  public Single<RoomStore> get(String storeName) {
    return RxJavaInterop.toV1Single(storeDao.getByStoreName(storeName))
        .onErrorReturn(throwable -> null)
        .subscribeOn(Schedulers.io());
  }

  public Single<RoomStore> get(long storeId) {
    return RxJavaInterop.toV1Single(storeDao.getByStoreId(storeId))
        .onErrorReturn(throwable -> null)
        .subscribeOn(Schedulers.io());
  }

  public Completable remove(String storeName) {
    return Completable.fromAction(() -> storeDao.removeByStoreName(storeName))
        .subscribeOn(Schedulers.io());
  }

  public Completable save(RoomStore store) {
    return Completable.fromAction(() -> storeDao.insert(store))
        .subscribeOn(Schedulers.io());
  }

  public Observable<Boolean> isSubscribed(long storeId) {
    return RxJavaInterop.toV1Observable(storeDao.isSubscribed(storeId), BackpressureStrategy.BUFFER)
        .map(count -> count > 0)
        .subscribeOn(Schedulers.io());
  }

  public Observable<Long> countAll() {
    return RxJavaInterop.toV1Observable(storeDao.countAll(), BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io());
  }

  @Override public Completable saveAll(List<RoomStore> stores) {
    return Completable.fromAction(() -> storeDao.saveAll(stores))
        .subscribeOn(Schedulers.io());
  }
}
