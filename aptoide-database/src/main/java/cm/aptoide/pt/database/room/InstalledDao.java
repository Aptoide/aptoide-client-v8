package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Observable;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface InstalledDao {

  @Query("SELECT * FROM Installed") Observable<RoomInstalled> getAll();

  @Query("SELECT * FROM Installed ORDER BY name ASC") Observable<RoomInstalled> getAllSortedAsc();

  @Query("DELETE FROM Installed where packageName = :packageName AND versionCode = :versionCode")
  Completable remove(String packageName, int versionCode);

  @Query("SELECT * FROM Installed where packageName = :packageName AND versionCode = :versionCode")
  Observable<RoomInstalled> get(String packageName, int versionCode);

  @Query("SELECT * FROM Installed where packageName = :packageName AND versionCode = :versionCode")
  Observable<List<RoomInstalled>> getAsList(String packageName, int versionCode);

  @Query("SELECT * FROM Installed where packageName = :packageName")
  Observable<List<RoomInstalled>> getAsListByPackageName(String packageName);

  @Query("SELECT * FROM Installed where packageName IN (:packageNames)")
  Observable<List<RoomInstalled>> getAsListByPackageList(String[] packageNames);

  @Insert(onConflict = REPLACE) void insertAll(List<RoomInstalled> installedList);

  @Insert(onConflict = REPLACE) void insert(RoomInstalled roomInstalled);
}