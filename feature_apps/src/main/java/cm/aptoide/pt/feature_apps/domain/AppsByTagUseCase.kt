package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class AppsByTagUseCase @Inject constructor(
  private val appsRepository: AppsRepository,
  private val urlsCache: UrlsCache
) : AppsListUseCase {

  /**
   * [source] - a tag/key of a cached URL
   */
  override suspend fun getAppsList(source: String): List<App> = urlsCache.get(id = source)
    ?.let {
      appsRepository.getAppsList(
        url = it,
        bypassCache = urlsCache.isInvalid(id = source)
      )
    }
    ?: throw IllegalStateException("No url cached")
}
