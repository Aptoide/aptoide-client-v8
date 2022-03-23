package cm.aptoide.pt.installedapps.data

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
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
  val localInstalledAppsRepository: LocalInstalledAppsRepository,
  val packageManager: PackageManager
) :
  InstalledAppsRepository {

  override suspend fun syncInstalledApps() {
    withContext(Dispatchers.IO) {
      val installedAppsList = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
      installedAppsList.sortWith { firstApp: PackageInfo, secondApp: PackageInfo -> ((firstApp.firstInstallTime - secondApp.firstInstallTime) / 1000).toInt() }
      localInstalledAppsRepository.addListInstalledApps(mapInstalledAppsList(installedAppsList))
    }
  }

  private fun mapInstalledAppsList(installedAppsList: List<PackageInfo>): ArrayList<InstalledAppEntity> {
    val installedList = ArrayList<InstalledAppEntity>()
    for (packageInfo in installedAppsList) {
      installedList.add(
        InstalledAppEntity(
          packageInfo.applicationInfo.loadLabel(packageManager).toString(),
          packageInfo.packageName,
          packageInfo.versionName,
          "android.resource://" + packageInfo.packageName + "/" + packageInfo.applicationInfo.icon
        )
      )
    }
    return installedList
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