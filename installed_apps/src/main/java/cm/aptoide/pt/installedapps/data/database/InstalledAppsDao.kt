package cm.aptoide.pt.installedapps.data.database

import androidx.room.*
import cm.aptoide.pt.installedapps.data.database.model.InstalledAppEntity
import cm.aptoide.pt.installedapps.data.database.model.InstalledState
import kotlinx.coroutines.flow.Flow

@Dao
@TypeConverters(
  InstalledStateTypeConverter::class
)
interface InstalledAppsDao : LocalInstalledAppsRepository {

  @Query("SELECT * from InstalledApps")
  override fun getInstalledApps(): Flow<List<InstalledAppEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  override fun addInstalledApp(installedAppEntity: InstalledAppEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  override fun addListInstalledApps(installedAppEntityList: List<InstalledAppEntity>)

  @Delete
  override fun removeInstalledApp(installedAppEntity: InstalledAppEntity)

  @Query("SELECT * from InstalledApps WHERE packageName=:packageName and versionCode=:versionCode")
  override fun getInstalledApp(versionCode: Int, packageName: String): Flow<InstalledAppEntity>

  @Query("SELECT * from InstalledApps WHERE packageName=:packageName and installedState=:installedState LIMIT 1")
  override fun getInstalledApp(
    packageName: String,
    installedState: InstalledState
  ): InstalledAppEntity

  @Query("SELECT * from InstalledApps WHERE installedState=:installedState")
  override fun getInstalledAppsByType(installedState: InstalledState): Flow<List<InstalledAppEntity>>
}
