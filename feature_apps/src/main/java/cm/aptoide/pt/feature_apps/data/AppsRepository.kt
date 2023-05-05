package cm.aptoide.pt.feature_apps.data

import kotlinx.coroutines.flow.Flow

interface AppsRepository {

  fun getAppsList(url: String, bypassCache: Boolean = false): Flow<List<App>>

  fun getAppsList(groupId: Long, bypassCache: Boolean = false): Flow<List<App>>

  fun getApp(packageName: String, bypassCache: Boolean = false): Flow<App>

  fun getRecommended(url: String, bypassCache: Boolean = false): Flow<List<App>>

  fun getCategoryAppsList(categoryName: String): Flow<List<App>>

  fun getAppVersions(packageName: String): Flow<List<App>>

}
