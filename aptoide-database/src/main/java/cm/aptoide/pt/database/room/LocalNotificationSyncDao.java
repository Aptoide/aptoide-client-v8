package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface LocalNotificationSyncDao {

  @Insert(onConflict = REPLACE) void save(RoomLocalNotificationSync roomLocalNotificationSync);

  @Query("SELECT * FROM localNotificationSync WHERE notificationId = :id")
  Observable<RoomLocalNotificationSync> get(String id);

  @Query("DELETE FROM localNotificationSync WHERE notificationId = :id") void delete(String id);
}
