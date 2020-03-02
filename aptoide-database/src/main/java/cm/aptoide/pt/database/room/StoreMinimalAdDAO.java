package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface StoreMinimalAdDAO {

  @Query("SELECT * FROM storeMinimalAd WHERE packageName = :packageName")
  Observable<RoomStoreMinimalAd> get(String packageName);

  @Insert(onConflict = REPLACE) void insert(RoomStoreMinimalAd roomStoreMinimalAd);

  @Delete void delete(RoomStoreMinimalAd roomStoreMinimalAd);
}
