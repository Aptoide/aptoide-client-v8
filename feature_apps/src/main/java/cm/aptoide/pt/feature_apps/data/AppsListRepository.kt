package cm.aptoide.pt.feature_apps.data

interface AppsListRepository {

  suspend fun getAppsList(url: String, bypassCache: Boolean = false): List<App>

  suspend fun getAppsList(storeId: Long, groupId: Long, bypassCache: Boolean = false): List<App>

  suspend fun getRecommended(path: String): List<App>

  suspend fun getCategoryAppsList(categoryName: String): List<App>

  suspend fun getAppVersions(packageName: String): List<App>

  suspend fun getAppsList(packageNames: String): List<App>
}