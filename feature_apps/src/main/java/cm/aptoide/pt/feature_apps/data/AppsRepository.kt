package cm.aptoide.pt.feature_apps.data

interface AppsRepository {

  suspend fun getAppsList(url: String, bypassCache: Boolean = false): List<App>

  suspend fun getAppsList(storeId: Long, groupId: Long, bypassCache: Boolean = false): List<App>

  suspend fun getApp(packageName: String, bypassCache: Boolean = false): App

  suspend fun getAppMeta(
    source: String,
    bypassCache: Boolean = false,
    useStoreName: Boolean = true
  ): App

  suspend fun getRecommended(path: String, bypassCache: Boolean = false): List<App>

  suspend fun getCategoryAppsList(categoryName: String): List<App>

  suspend fun getAppVersions(packageName: String): List<App>

  suspend fun getAppsList(packageNames: String): List<App>

  suspend fun getAppsDynamicSplits(md5: String): List<DynamicSplit>
}
