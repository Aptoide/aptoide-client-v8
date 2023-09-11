package cm.aptoide.pt.task_info.database

import androidx.room.*
import cm.aptoide.pt.task_info.database.model.TaskInfoData
import cm.aptoide.pt.task_info.database.model.TaskInfoWithFiles

@Dao
interface TaskInfoDao {

  @Transaction
  @Query("SELECT * from TaskInfo")
  suspend fun getAll(): List<TaskInfoWithFiles>

  @Transaction
  @Query("SELECT * from TaskInfo WHERE waitForWifi = 1")
  suspend fun getPendingWifiTasks(): List<TaskInfoWithFiles>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun save(appInfoEntity: TaskInfoData)

  @Query("DELETE FROM TaskInfo WHERE packageName = :packageName")
  suspend fun remove(packageName: String)
}
