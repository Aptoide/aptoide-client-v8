package cm.aptoide.pt.database;

import cm.aptoide.pt.database.room.RoomStoreMinimalAd;
import cm.aptoide.pt.database.room.StoreMinimalAdDAO;
import cm.aptoide.pt.logger.Logger;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import io.reactivex.schedulers.Schedulers;
import rx.Observable;

public class RoomStoreMinimalAdPersistence implements StoreMinimalAdPersistence {

  private final StoreMinimalAdDAO storeMinimalAdDAO;

  public RoomStoreMinimalAdPersistence(StoreMinimalAdDAO storeMinimalAdDAO) {
    this.storeMinimalAdDAO = storeMinimalAdDAO;
  }

  @Override public Observable<RoomStoreMinimalAd> get(String packageName) {
    return RxJavaInterop.toV1Observable(storeMinimalAdDAO.get(packageName)
        .subscribeOn(Schedulers.io())
        .onErrorReturn(throwable -> null)
        .doOnError(Throwable::printStackTrace), BackpressureStrategy.BUFFER);
  }

  @Override public void remove(RoomStoreMinimalAd storeMinimalAd) {
    storeMinimalAdDAO.delete(storeMinimalAd);
  }

  @Override public void insert(RoomStoreMinimalAd storeMinimalAd) {
    storeMinimalAdDAO.insert(storeMinimalAd);
  }
}
