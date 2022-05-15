package cm.aptoide.pt.feature_appview.domain.repository

import cm.aptoide.pt.feature_apps.data.App
import kotlinx.coroutines.flow.Flow

interface AppViewRepository {
  fun getAppInfo(packageName: String): Flow<AppViewResult>
  fun getSimilarApps(packageName: String): Flow<SimilarAppsResult>
}

sealed interface AppViewResult {
  data class Success(val data: App) : AppViewResult
  data class Error(val error: Throwable) : AppViewResult
}

sealed interface SimilarAppsResult {
  data class Success(val similarApps: List<App>, val appcSimilarApps: List<App>) : SimilarAppsResult
  data class Error(val error: Throwable) : SimilarAppsResult
}
