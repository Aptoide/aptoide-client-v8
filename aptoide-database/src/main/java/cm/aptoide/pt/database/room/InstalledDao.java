package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface InstalledDao {

  @Query("SELECT * FROM Installed") Observable<List<RoomInstalled>> getAll();

  @Query("SELECT * FROM Installed ORDER BY name ASC")
  Observable<List<RoomInstalled>> getAllSortedAsc();

  @Query("DELETE FROM Installed where packageName = :packageName AND versionCode = :versionCode")
  Completable remove(String packageName, int versionCode);

  @Query("DELETE FROM Installed where packageName = :packageName ")
  Completable remove(String packageName);

  @Query("SELECT * FROM Installed where packageName = :packageName AND versionCode = :versionCode LIMIT 1")
  Observable<RoomInstalled> get(String packageName, int versionCode);

  @Query("SELECT * FROM Installed where packageName = :packageName AND versionCode = :versionCode")
  Observable<List<RoomInstalled>> getAsList(String packageName, int versionCode);

  @Query("SELECT * FROM Installed where packageName = :packageName")
  Observable<List<RoomInstalled>> getAsListByPackageName(String packageName);

  @Insert(onConflict = REPLACE) void insertAll(List<RoomInstalled> installedList);

  @Insert(onConflict = REPLACE) void insert(RoomInstalled roomInstalled);

  @Query("DELETE FROM installed") void removeAll();

  @Query("SELECT * FROM installed where packageName = :packageName AND versionCode = :versionCode")
  Single<RoomInstalled> isInstalledByVersion(String packageName, int versionCode);

  @Query("SELECT * FROM Installed where systemApp = 0 ORDER BY name ASC")
  Observable<List<RoomInstalled>> getAllFilteringSystemApps();
}