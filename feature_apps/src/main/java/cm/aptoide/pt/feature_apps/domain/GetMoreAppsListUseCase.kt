package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.BundlesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoreAppsListUseCase @Inject constructor(private val bundlesRepository: BundlesRepository) {

  fun getMoreAppsList(bundleIdentifier: String): Flow<List<App>> {
    return bundlesRepository.getHomeBundleActionListApps(bundleIdentifier)
  }
}