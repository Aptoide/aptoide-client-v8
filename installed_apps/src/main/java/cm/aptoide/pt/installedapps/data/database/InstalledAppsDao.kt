package cm.aptoide.pt.installedapps.data.database

import androidx.room.*
import cm.aptoide.pt.installedapps.data.database.model.InstalledAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstalledAppsDao : LocalInstalledAppsRepository {

  @Query("SELECT * from InstalledApps")
  override fun getInstalledApps(): Flow<List<InstalledAppEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  override fun addInstalledApp(installedAppEntity: InstalledAppEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  override fun addListInstalledApps(installedAppEntityList: List<InstalledAppEntity>)

  @Delete
  override fun removeInstalledApp(installedAppEntity: InstalledAppEntity)
}
