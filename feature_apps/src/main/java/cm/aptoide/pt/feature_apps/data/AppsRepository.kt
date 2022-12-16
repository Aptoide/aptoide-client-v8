package cm.aptoide.pt.feature_apps.data

import kotlinx.coroutines.flow.Flow

interface AppsRepository {

  fun getAppsList(url: String): Flow<AppsResult>

  fun getAppsList(groupId: Long): Flow<AppsResult>

  fun getApp(packageName: String): Flow<AppResult>

  fun getRecommended(url: String): Flow<AppsResult>

  fun getAppVersions(packageName: String): Flow<AppsResult>

  suspend fun getAppGroupsList(packageName: String, groupId: Long? = null): GroupsResult
}

sealed interface AppsResult {
  data class Success(val data: List<App>) : AppsResult
  data class Error(val e: Throwable) : AppsResult
}

sealed interface AppResult {
  data class Success(val data: App) : AppResult
  data class Error(val e: Throwable) : AppResult
}

sealed interface GroupsResult {
  data class Success(val data: List<Group>) : GroupsResult
  data class Error(val e: Throwable) : GroupsResult
}
