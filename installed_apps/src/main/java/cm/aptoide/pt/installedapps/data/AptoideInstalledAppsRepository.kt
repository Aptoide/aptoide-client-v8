package cm.aptoide.pt.installedapps.data

import cm.aptoide.pt.installedapps.data.database.LocalInstalledAppsRepository
import cm.aptoide.pt.installedapps.data.database.model.InstalledAppEntity
import cm.aptoide.pt.installedapps.data.database.model.InstalledState
import cm.aptoide.pt.installedapps.domain.model.InstalledApp
import cm.aptoide.pt.installedapps.domain.model.InstalledAppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideInstalledAppsRepository @Inject constructor(
  private val localInstalledAppsRepository: LocalInstalledAppsRepository,
  private val installedAppsProvider: InstalledAppsProvider,
  private val installedAppStateMapper: InstalledAppStateMapper
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
            installedAppEntity.versionCode,
            installedAppEntity.appIcon,
            installedAppStateMapper.mapInstalledAppState(installedAppEntity.installedState)
          )
        }
      }
  }

  override fun getInstalledApp(versionCode: Int, packageName: String): Flow<InstalledApp> {
    return localInstalledAppsRepository.getInstalledApp(versionCode, packageName)
      .map { installedAppEntity ->
        if (installedAppEntity == null) {
          InstalledApp("", packageName, "", versionCode, "", InstalledAppState.NOT_INSTALLED)
        } else {
          InstalledApp(
            installedAppEntity.appName,
            installedAppEntity.packageName,
            installedAppEntity.appVersion,
            installedAppEntity.versionCode,
            installedAppEntity.appIcon,
            installedAppStateMapper.mapInstalledAppState(installedAppEntity.installedState)
          )
        }
      }.catch { throwable -> throwable.printStackTrace() }
  }

  override fun getInstalledApp(packageName: String): Flow<InstalledApp> {
    return flow {
      try {
        val installedApp =
          localInstalledAppsRepository.getInstalledApp(packageName, InstalledState.INSTALLED)
        emit(
          InstalledApp(
            installedApp.appName,
            installedApp.packageName,
            installedApp.appVersion,
            installedApp.versionCode,
            installedApp.appIcon,
            installedAppStateMapper.mapInstalledAppState(installedApp.installedState)
          )
        )

      } catch (e: Exception) {
        e.printStackTrace()
        emit(
          InstalledApp("", packageName, "", 0, "", InstalledAppState.NOT_INSTALLED)
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

  override fun getDownloadInstallApps(): Flow<List<InstalledApp>> {
    return localInstalledAppsRepository.getInstalledAppsByType(InstalledState.DOWNLOADING).map {
      it.map { installedAppEntity ->
        InstalledApp(
          installedAppEntity.appName,
          installedAppEntity.packageName,
          installedAppEntity.appVersion,
          installedAppEntity.versionCode,
          installedAppEntity.appIcon,
          installedAppStateMapper.mapInstalledAppState(installedAppEntity.installedState)
        )
      }
    }
  }
}