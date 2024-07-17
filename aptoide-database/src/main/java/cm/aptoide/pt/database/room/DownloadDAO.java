package cm.aptoide.pt.database.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao public interface DownloadDAO {

  @Query("SELECT * from download") Observable<List<RoomDownload>> getAll();

  @Query("SELECT * from download where md5 = :md5 LIMIT 1 ") Single<RoomDownload> getAsSingle(
      String md5);

  @Query("SELECT * from download where md5 = :md5 LIMIT 1 ")
  Observable<RoomDownload> getAsObservable(String md5);

  @Query("DELETE from download where md5= :md5") void remove(String md5);

  @Query("DELETE from download where packageName=:packageName and versionCode=:versionCode")
  void remove(String packageName, int versionCode);

  @Insert(onConflict = REPLACE) void insertAll(List<RoomDownload> downloads);

  @Insert(onConflict = REPLACE) void insert(RoomDownload download);

  @Query("SELECT * from download where overallDownloadStatus = "
      + RoomDownload.PROGRESS
      + " OR overallDownloadStatus = "
      + RoomDownload.IN_QUEUE
      + " OR overallDownloadStatus = "
      + RoomDownload.PENDING) Observable<List<RoomDownload>> getRunningDownloads();

  @Query("SELECT * from download where overallDownloadStatus="
      + RoomDownload.IN_QUEUE
      + " ORDER BY timeStamp ASC") Observable<List<RoomDownload>> getInQueueSortedDownloads();

  @Query("SELECT * from download where md5 = :md5") Observable<List<RoomDownload>> getAsList(
      String md5);

  @Query("SELECT * from download where overallDownloadStatus="
      + RoomDownload.ERROR
      + " and downloadError="
      + RoomDownload.NOT_ENOUGH_SPACE_ERROR
      + " ORDER BY timeStamp ASC") Observable<List<RoomDownload>> getOutOfSpaceDownloads();
}
