package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface MigratedAppDAO {

  @Query("SELECT COUNT(*) from migratedapp where packageName like :packageName")
  Observable<Integer> isAppMigrated(String packageName);

  @Insert(onConflict = REPLACE) void save(RoomMigratedApp roomMigratedApp);
}
