package cm.aptoide.pt.database;

import cm.aptoide.pt.database.room.RoomStoredMinimalAd;
import rx.Observable;

public interface StoredMinimalAdPersistence {

  Observable<RoomStoredMinimalAd> get(String packageName);

  void remove(RoomStoredMinimalAd storedMinimalAd);

  void insert(RoomStoredMinimalAd storedMinimalAd);
}
