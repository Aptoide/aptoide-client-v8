package cm.aptoide.pt.feature_updates.data

import cm.aptoide.pt.feature_updates.domain.model.InstalledApp
import cm.aptoide.pt.feature_updates.domain.repository.UpdatesRepository
import cm.aptoide.pt.installedapps.data.database.LocalInstalledAppsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideUpdatesRepository @Inject constructor(private val installedAppsRepository: LocalInstalledAppsRepository) :
  UpdatesRepository {

  override fun getInstalledApps(): Flow<List<InstalledApp>> {
    return installedAppsRepository.getInstalledApps().map {
      it.map { installedAppEntity ->
        InstalledApp(
          installedAppEntity.appName,
          installedAppEntity.packageName,
          installedAppEntity.appVersion, installedAppEntity.appIcon
        )
      }
    }
  }
}