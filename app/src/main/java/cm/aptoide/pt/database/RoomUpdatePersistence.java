package cm.aptoide.pt.database;

import cm.aptoide.pt.database.room.RoomUpdate;
import cm.aptoide.pt.database.room.UpdateDao;
import cm.aptoide.pt.updates.UpdatePersistence;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 9/2/16.
 */
public class RoomUpdatePersistence implements UpdatePersistence {

  private final UpdateDao updateDao;

  public RoomUpdatePersistence(UpdateDao updateDao) {
    this.updateDao = updateDao;
  }

  public Single<RoomUpdate> get(String packageName) {
    return RxJavaInterop.toV1Single(updateDao.get(packageName))
        .onErrorReturn(throwable -> null)
        .subscribeOn(Schedulers.io());
  }

  public Single<List<RoomUpdate>> getAll(boolean isExcluded) {
    return RxJavaInterop.toV1Single(updateDao.getAllByExcluded(isExcluded))
        .onErrorReturn(throwable -> new ArrayList<>())
        .subscribeOn(Schedulers.io());
  }

  public Observable<List<RoomUpdate>> getAllSorted(boolean isExcluded) {
    return RxJavaInterop.toV1Observable(updateDao.getAllByExcludedSorted(isExcluded),
        BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io());
  }

  public Single<Boolean> contains(String packageName, boolean isExcluded) {
    return RxJavaInterop.toV1Single(updateDao.getByPackageAndExcluded(packageName, isExcluded))
        .onErrorReturn(throwable -> null)
        .map(update -> update != null)
        .subscribeOn(Schedulers.io());
  }

  public Completable remove(String packageName) {
    return Completable.fromAction(() -> updateDao.deleteByPackageName(packageName))
        .subscribeOn(Schedulers.io());
  }

  public Completable removeAll(List<RoomUpdate> roomUpdatesList) {
    return Completable.fromAction(() -> updateDao.deleteAll(roomUpdatesList))
        .subscribeOn(Schedulers.io());
  }

  public Completable saveAll(List<RoomUpdate> updates) {
    return Completable.fromAction(() -> updateDao.insertAll(updates))
        .subscribeOn(Schedulers.io());
  }

  public Completable save(RoomUpdate update) {
    return Completable.fromAction(() -> updateDao.insert(update))
        .subscribeOn(Schedulers.io());
  }

  public Single<Boolean> isExcluded(String packageName) {
    return RxJavaInterop.toV1Single(updateDao.getByPackageAndExcluded(packageName, true)
        .map(update -> update != null))
        .onErrorReturn(throwable -> false)
        .subscribeOn(Schedulers.io());
  }
}
