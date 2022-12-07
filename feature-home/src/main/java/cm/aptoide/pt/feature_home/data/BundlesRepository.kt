package cm.aptoide.pt.feature_home.data

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_home.domain.Bundle
import kotlinx.coroutines.flow.Flow

interface BundlesRepository {

  fun getHomeBundles(): Flow<BundlesResult>

  fun getHomeBundleActionListApps(bundleTag: String): Flow<List<App>>

}

sealed interface BundlesResult {
  data class Success(val data: List<Bundle>) : BundlesResult
  data class Error(val e: Throwable) : BundlesResult
}
