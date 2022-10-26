package cm.aptoide.pt.installedapps.data.database

import cm.aptoide.pt.installedapps.data.database.model.InstalledAppEntity
import cm.aptoide.pt.installedapps.data.database.model.InstalledState
import kotlinx.coroutines.flow.Flow

interface LocalInstalledAppsRepository {

  fun getInstalledApps(): Flow<List<InstalledAppEntity>>

  fun addInstalledApp(installedAppEntity: InstalledAppEntity)

  fun addListInstalledApps(installedAppEntityList: List<InstalledAppEntity>)

  fun removeInstalledApp(installedAppEntity: InstalledAppEntity)

  fun getInstalledApp(versionCode: Int, packageName: String): Flow<InstalledAppEntity>

  fun getInstalledAppsByType(installedState: InstalledState): Flow<List<InstalledAppEntity>>

  fun getInstalledApp(packageName: String, installedState: InstalledState): InstalledAppEntity
}