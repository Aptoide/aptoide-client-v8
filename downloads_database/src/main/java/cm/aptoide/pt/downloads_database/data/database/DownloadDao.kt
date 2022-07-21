package cm.aptoide.pt.downloads_database.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {

  @Query("SELECT * from download")
  fun getAllDownloads(): Flow<List<DownloadEntity>>

  @Query("SELECT * from download where md5 = :md5 LIMIT 1 ")
  suspend fun getDownload(md5: String): DownloadEntity

  @Query("SELECT * from download where md5 = :md5 LIMIT 1 ")
  fun observeDownload(md5: String): Flow<DownloadEntity>

  @Query("DELETE from download where md5= :md5")
  fun removeDownload(md5: String)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveDownload(downloadEntity: DownloadEntity)

  @Query(
    "SELECT * from download where overallDownloadStatus = "
        + DownloadEntity.PROGRESS
        + " OR overallDownloadStatus = "
        + DownloadEntity.IN_QUEUE
        + " OR overallDownloadStatus = "
        + DownloadEntity.PENDING
  )
  fun getRunningDownloads(): Flow<List<DownloadEntity>>

  @Query(
    "SELECT * from download where overallDownloadStatus="
        + DownloadEntity.IN_QUEUE
        + " ORDER BY timeStamp ASC"
  )
  fun getInQueueDownloads(): Flow<List<DownloadEntity>>

  @Query(
    "SELECT * from download where overallDownloadStatus="
        + DownloadEntity.WAITING_TO_MOVE_FILES
        + " ORDER BY timeStamp ASC"
  )
  fun getUnmovedDownloads(): Flow<List<DownloadEntity>>

}