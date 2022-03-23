package cm.aptoide.pt.feature_updates.data

import cm.aptoide.pt.feature_updates.domain.repository.UpdatesRepository
import cm.aptoide.pt.installedapps.data.InstalledAppsRepository
import cm.aptoide.pt.installedapps.domain.model.InstalledApp
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideUpdatesRepository @Inject constructor(private val installedAppsRepository: InstalledAppsRepository) :
  UpdatesRepository {

  override fun getInstalledApps(): Flow<List<InstalledApp>> {
    return installedAppsRepository.getInstalledApps()
  }
}