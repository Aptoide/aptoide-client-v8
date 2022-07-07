package cm.aptoide.pt.feature_appview.domain.repository

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_appview.domain.model.RelatedCard
import kotlinx.coroutines.flow.Flow

interface AppViewRepository {
  fun getAppInfo(packageName: String): Flow<AppViewResult>
  fun getSimilarApps(packageName: String): Flow<SimilarAppsResult>
  fun getAppcSimilarApps(packageName: String): Flow<SimilarAppsResult>
  fun getOtherVersions(packageName: String): Flow<OtherVersionsResult>
  fun getRelatedContent(packageName: String): Flow<RelatedContentResult>
}

sealed interface AppViewResult {
  data class Success(val data: App) : AppViewResult
  data class Error(val error: Throwable) : AppViewResult
}

sealed interface SimilarAppsResult {
  data class Success(val similarApps: List<App>) : SimilarAppsResult
  data class Error(val error: Throwable) : SimilarAppsResult
}

sealed interface OtherVersionsResult {
  data class Success(val otherVersionsList: List<App>) : OtherVersionsResult
  data class Error(val error: Throwable) : OtherVersionsResult
}

sealed interface RelatedContentResult {
  data class Success(val relatedContent: List<RelatedCard>) : RelatedContentResult
  data class Error(val error: Throwable) : RelatedContentResult
}
