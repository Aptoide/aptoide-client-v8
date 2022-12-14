package cm.aptoide.pt.feature_home.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_home.data.BundlesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoreAppsListUseCase @Inject constructor(private val bundlesRepository: BundlesRepository) {

  fun getMoreAppsList(bundleTag: String): Flow<List<App>> {
    return bundlesRepository.getHomeBundleActionListApps(bundleTag)
  }
}