package cm.aptoide.pt.installedapps.data

import cm.aptoide.pt.installedapps.data.database.model.InstalledAppEntity
import cm.aptoide.pt.installedapps.domain.model.InstalledApp
import kotlinx.coroutines.flow.Flow

interface InstalledAppsRepository {

  suspend fun syncInstalledApps()

  fun getInstalledApps(): Flow<List<InstalledApp>>

  fun getInstalledApp(versionCode: Int, packageName: String): Flow<InstalledApp>

  fun getInstalledApp(packageName: String): Flow<InstalledApp>

  fun addInstalledApp(installedAppEntity: InstalledAppEntity)

  fun addListInstalledApps(installedAppEntityList: List<InstalledAppEntity>)

  fun removeInstalledApp(installedAppEntity: InstalledAppEntity)

  fun getDownloadInstallApps(): Flow<List<InstalledApp>>
}