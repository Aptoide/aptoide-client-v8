package cm.aptoide.pt.database;

import cm.aptoide.pt.database.room.RoomStoreMinimalAd;
import rx.Observable;

public interface StoreMinimalAdPersistence {

  Observable<RoomStoreMinimalAd> get(String packageName);

  void remove(RoomStoreMinimalAd storeMinimalAd);

  void insert(RoomStoreMinimalAd storeMinimalAd);
}
