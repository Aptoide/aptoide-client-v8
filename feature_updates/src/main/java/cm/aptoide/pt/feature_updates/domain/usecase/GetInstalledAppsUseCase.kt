package cm.aptoide.pt.feature_updates.domain.usecase

import cm.aptoide.pt.feature_updates.domain.repository.UpdatesRepository
import cm.aptoide.pt.installedapps.domain.model.InstalledApp
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetInstalledAppsUseCase @Inject constructor(private val updatesRepository: UpdatesRepository) {

  fun getInstalledApps(): Flow<List<InstalledApp>> {
    return updatesRepository.getInstalledApps()
  }
}