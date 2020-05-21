package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface AptoideInstallDao {

  @Query("SELECT * from aptoideinstallapp where packageName = :packageName LIMIT 1")
  Single<RoomAptoideInstallApp> get(String packageName);

  @Insert(onConflict = REPLACE) void insert(RoomAptoideInstallApp roomAptoideInstallApp);
}
