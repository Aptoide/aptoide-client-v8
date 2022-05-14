package cm.aptoide.pt.feature_appview.domain.repository

import cm.aptoide.pt.feature_apps.data.App
import kotlinx.coroutines.flow.Flow

interface AppViewRepository {
  fun getAppInfo(packageName: String): Flow<AppViewResult>
}

sealed interface AppViewResult {
  data class Success(val data: App) : AppViewResult
  data class Error(val error: Throwable) : AppViewResult
}