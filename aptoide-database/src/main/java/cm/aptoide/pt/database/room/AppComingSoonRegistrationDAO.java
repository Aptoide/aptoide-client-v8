package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface AppComingSoonRegistrationDAO {

  @Query("SELECT COUNT(*) from appComingSoonRegistration where packageName like :packageName")
  Observable<Integer> isRegisteredForApp(String packageName);

  @Insert(onConflict = REPLACE) void save(
      RoomAppComingSoonRegistration roomAppComingSoonRegistration);

  @Delete void remove(RoomAppComingSoonRegistration roomAppComingSoonRegistration);
}
