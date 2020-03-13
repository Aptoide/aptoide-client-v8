package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface NotificationDao {

  @Query("SELECT * from notification where type IN (:notificationType) AND dismissed BETWEEN :startingTime AND :endTime ")
  Single<List<RoomNotification>> getDismissed(Integer[] notificationType, long startingTime,
      long endTime);

  @Query("SELECT * from notification where type IN (:notificationType) ORDER BY timeStamp DESC")
  Single<List<RoomNotification>> getAllSortedDescByType(Integer[] notificationType);

  @Query("SELECT * from notification ORDER BY timeStamp DESC")
  Observable<List<RoomNotification>> getAllSortedDesc();

  @Query("SELECT * FROM notification") Observable<List<RoomNotification>> getAll();

  @Query("DELETE FROM notification where ownerId NOT IN (:ids) ") void deleteAllExcluding(
      List<String> ids);

  @Query("DELETE FROM notification where `key` IN (:keys) ") void deleteByKey(List<String> keys);

  @Query("DELETE FROM notification where type = :type") void deleteAllByType(int type);

  @Insert(onConflict = REPLACE) void insertAll(List<RoomNotification> notifications);

  @Insert(onConflict = REPLACE) void insert(RoomNotification notification);
}