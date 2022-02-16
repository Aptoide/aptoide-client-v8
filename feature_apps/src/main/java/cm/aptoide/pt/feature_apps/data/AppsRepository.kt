package cm.aptoide.pt.feature_apps.data

import kotlinx.coroutines.flow.Flow

interface AppsRepository {

  fun getAppsList(url: String): Flow<AppsResult>

}

sealed interface AppsResult {
  data class Success(val data: List<App>) : AppsResult
  data class Error(val e: Throwable) : AppsResult

}
