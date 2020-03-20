package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface UpdateDao {

  @Query("SELECT * from `update` where packageName = :packageName") Observable<RoomUpdate> get(
      String packageName);

  @Query("SELECT * from `update` where excluded = :isExcluded")
  Single<List<RoomUpdate>> getAllByExcluded(boolean isExcluded);

  @Query("SELECT * from `update` where excluded = :isExcluded ORDER BY label")
  Observable<List<RoomUpdate>> getAllByExcludedSorted(boolean isExcluded);

  @Query("SELECT * from `update` where excluded = :isExcluded and packageName = :packageName LIMIT 1")
  Single<RoomUpdate> containsByPackageAndExcluded(String packageName, boolean isExcluded);

  @Query("SELECT * from `update` where excluded = :isExcluded and packageName = :packageName and appcUpgrade = :isAppcUpgrade LIMIT 1")
  Single<RoomUpdate> containsByPackageAndExcludedAndUpgrade(String packageName, boolean isExcluded,
      boolean isAppcUpgrade);

  @Query("DELETE from `update` where packageName = :packageName") void deleteByPackageName(
      String packageName);

  @Delete void deleteAll(List<RoomUpdate> updatesList);

  @Insert(onConflict = REPLACE) void insert(RoomUpdate update);

  @Insert(onConflict = REPLACE) void insertAll(List<RoomUpdate> updatesList);

  @Query("SELECT * from `update` where excluded = :isExcluded and packageName = :packageName LIMIT 1")
  Single<RoomUpdate> isExcluded(String packageName, boolean isExcluded);
}

