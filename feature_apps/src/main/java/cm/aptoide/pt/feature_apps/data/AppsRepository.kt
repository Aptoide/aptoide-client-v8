package cm.aptoide.pt.feature_apps.data

import kotlinx.coroutines.flow.Flow

interface AppsRepository {

  fun getAppsList(url: String): Flow<List<App>>

  fun getAppsList(groupId: Long): Flow<List<App>>

  fun getApp(packageName: String): Flow<App>

  fun getRecommended(url: String): Flow<List<App>>

  fun getAppVersions(packageName: String): Flow<List<App>>

  suspend fun getAppsCategories(packageNames: List<String>): List<AppCategory>
}
