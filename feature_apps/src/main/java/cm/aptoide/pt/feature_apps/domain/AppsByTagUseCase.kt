package cm.aptoide.pt.feature_apps.domain

import android.net.Uri
import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListRepository
import javax.inject.Inject

class AppsByTagUseCase @Inject constructor(
  private val appsListRepository: AppsListRepository,
  private val urlsCache: UrlsCache,
) : AppsListUseCase {

  /**
   * [source] - a tag/key of a cached URL
   */
  override suspend fun getAppsList(source: String): List<App> = urlsCache.get(id = source)
    ?.let {
      appsListRepository.getAppsList(
        url = it,
        bypassCache = urlsCache.isInvalid(id = source)
      )
    }
    ?: throw IllegalStateException("No url cached")

  suspend fun getAppsListWithLimit(
    source: String,
    limit: Int,
  ): List<App> {
    return urlsCache.get(id = source)
      ?.let {
        val newUrl = Uri.parse(it).buildUpon().appendPath("limit=$limit").build().toString()
        appsListRepository.getAppsList(
          url = newUrl,
          bypassCache = urlsCache.isInvalid(id = source)
        )
      }
      ?: throw IllegalStateException("No url cached")
  }
}
