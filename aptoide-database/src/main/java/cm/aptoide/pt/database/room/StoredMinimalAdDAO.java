package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface StoredMinimalAdDAO {

  @Query("SELECT * FROM storedMinimalAd WHERE packageName = :packageName")
  Observable<RoomStoredMinimalAd> get(String packageName);

  @Insert(onConflict = REPLACE) void insert(RoomStoredMinimalAd roomStoredMinimalAd);

  @Delete void delete(RoomStoredMinimalAd roomStoredMinimalAd);
}
