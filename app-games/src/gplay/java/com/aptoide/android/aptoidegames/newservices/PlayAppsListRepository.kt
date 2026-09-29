package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.aptoide_network.di.V7Backend
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource.Companion.SORT_ALPHA
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource.Companion.SORT_DOWNLOADS
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource.Companion.SORT_LATEST
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource.Companion.SORT_TRENDING
import cm.aptoide.pt.feature_categories.data.deviceapi.DeviceApiCategoriesDataSource.Companion.PARENT_GAMES
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The apps lists of the Play build. The catalog is read from the new services, and v7 keeps
 * what only it serves: the AppCoins listings, and whatever is addressed the v7 way - an url,
 * a store group, a list of packages.
 *
 * Each read goes to exactly one of the two, decided by what is asked.
 */
@Singleton
internal class PlayAppsListRepository @Inject constructor(
  @V7Backend private val v7: AppsListRepository,
  private val newServices: DeviceApiAppsDataSource,
) : AppsListRepository {

  override suspend fun getAppsList(url: String, bypassCache: Boolean): List<App> =
    NewServicesListingUrl.parse(url)
      ?.let {
        newServices.browse(
          category = it.category,
          sort = it.sort,
          limit = it.limit,
          refresh = bypassCache,
        )
      }
      ?: v7.getAppsList(url, bypassCache)

  override suspend fun getAppsList(
    storeId: Long,
    groupId: Long,
    bypassCache: Boolean,
  ): List<App> = v7.getAppsList(storeId, groupId, bypassCache)

  override suspend fun getRecommended(path: String): List<App> =
    path.packageNameOnly()
      ?.let { newServices.related(packageName = it) }
      ?: v7.getRecommended(path)

  override suspend fun getCategoryAppsList(categoryName: String): List<App> =
    newServices.browse(category = categoryName, limit = CATEGORY_PAGE)

  override suspend fun getAppVersions(packageName: String): List<App> =
    v7.getAppVersions(packageName)

  override suspend fun getAppsList(packageNames: String): List<App> =
    v7.getAppsList(packageNames)

  override suspend fun getSortedAppsList(sort: String, limit: Int): List<App> =
    if (sort.startsWith(APPCOINS_SORT_PREFIX)) {
      v7.getSortedAppsList(sort, limit)
    } else {
      newServices.browse(
        category = PARENT_GAMES,
        sort = sort.toNewServicesSort(),
        limit = limit,
      )
    }

  // The package of a path that names nothing but one. Anything more, such as the AppCoins
  // section, is a v7 notion.
  private fun String.packageNameOnly(): String? =
    removePrefix(PACKAGE_NAME)
      .takeIf { startsWith(PACKAGE_NAME) && it.isNotBlank() && "/" !in it }

  private companion object {
    const val PACKAGE_NAME = "package_name="
    const val APPCOINS_SORT_PREFIX = "appc"

    // As many as a category shows in v7, which has no limit of its own and answers with 50
    const val CATEGORY_PAGE = 50
  }
}

/** The sort of the new services closest to a sort named the v7 way, downloads if none is. */
internal fun String.toNewServicesSort(): String = when {
  contains("trending", ignoreCase = true) -> SORT_TRENDING
  contains("download", ignoreCase = true) -> SORT_DOWNLOADS
  contains("alpha", ignoreCase = true) -> SORT_ALPHA
  contains("latest", ignoreCase = true) ||
    contains("updated", ignoreCase = true) ||
    contains("added", ignoreCase = true) -> SORT_LATEST

  else -> SORT_DOWNLOADS
}
