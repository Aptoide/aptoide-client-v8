package cm.aptoide.pt.task_info.database

import androidx.room.*
import cm.aptoide.pt.task_info.database.model.InstallationFileData

@Dao
interface InstallationFileDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun save(appInfoEntity: List<InstallationFileData>)

  @Query("DELETE FROM InstallationFile WHERE packageName = :packageName")
  suspend fun remove(packageName: String)
}
