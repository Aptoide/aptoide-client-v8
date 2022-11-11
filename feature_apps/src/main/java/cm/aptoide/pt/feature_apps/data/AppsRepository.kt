package cm.aptoide.pt.feature_apps.data

import kotlinx.coroutines.flow.Flow

interface AppsRepository {

  fun getAppsList(url: String): Flow<AppsResult>

  fun getAppsList(groupId: Long): Flow<AppsResult>

  fun getApp(packageName: String): Flow<AppResult>

  fun getRecommended(url: String): Flow<AppsResult>

  fun getAppVersions(packageName: String): Flow<AppsResult>
}

sealed interface AppsResult {
  data class Success(val data: List<App>) : AppsResult
  data class Error(val e: Throwable) : AppsResult
}

sealed interface AppResult {
  data class Success(val data: DetailedApp) : AppResult
  data class Error(val e: Throwable) : AppResult
}
