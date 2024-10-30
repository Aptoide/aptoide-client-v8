package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsListRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ESkillsAppsUseCase @Inject constructor(
  private val appsListRepository: AppsListRepository,
  private val urlsCache: UrlsCache
) : AppsListUseCase {

  /**
   * [source] - unused
   */
  override suspend fun getAppsList(source: String): List<App> =
    appsListRepository.getAppsList(
      storeId = 15,
      groupId = 14169744,
      bypassCache = urlsCache.isInvalid(id = "eSkills/15/14169744")
    )
}
