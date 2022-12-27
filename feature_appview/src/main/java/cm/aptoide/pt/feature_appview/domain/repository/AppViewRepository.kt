package cm.aptoide.pt.feature_appview.domain.repository

import cm.aptoide.pt.feature_apps.data.App
import kotlinx.coroutines.flow.Flow

interface AppViewRepository {
  fun getAppInfo(packageName: String): Flow<App>
  fun getSimilarApps(packageName: String): Flow<List<App>>
  fun getAppcSimilarApps(packageName: String): Flow<List<App>>
  fun getOtherVersions(packageName: String): Flow<List<App>>
}
