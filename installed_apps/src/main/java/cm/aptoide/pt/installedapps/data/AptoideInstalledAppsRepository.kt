package cm.aptoide.pt.installedapps.data

import cm.aptoide.pt.installedapps.data.database.LocalInstalledAppsRepository
import cm.aptoide.pt.installedapps.data.database.model.InstalledAppEntity
import cm.aptoide.pt.installedapps.domain.model.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideInstalledAppsRepository @Inject constructor(
  private val localInstalledAppsRepository: LocalInstalledAppsRepository,
  private val installedAppsProvider: InstalledAppsProvider
) :
  InstalledAppsRepository {

  override suspend fun syncInstalledApps() {
    withContext(Dispatchers.IO) {
      localInstalledAppsRepository.addListInstalledApps(installedAppsProvider.getInstalledApps())
    }
  }


  override fun getInstalledApps(): Flow<List<InstalledApp>> {
    return localInstalledAppsRepository.getInstalledApps()
      .map {
        it.map { installedAppEntity ->
          InstalledApp(
            installedAppEntity.appName,
            installedAppEntity.packageName,
            installedAppEntity.appVersion,
            installedAppEntity.appIcon
          )
        }
      }
  }

  override fun addInstalledApp(installedAppEntity: InstalledAppEntity) {
    localInstalledAppsRepository.addInstalledApp(installedAppEntity)
  }

  override fun addListInstalledApps(installedAppEntityList: List<InstalledAppEntity>) {
    localInstalledAppsRepository.addListInstalledApps(installedAppEntityList)
  }

  override fun removeInstalledApp(installedAppEntity: InstalledAppEntity) {
    localInstalledAppsRepository.removeInstalledApp(installedAppEntity)
  }


}