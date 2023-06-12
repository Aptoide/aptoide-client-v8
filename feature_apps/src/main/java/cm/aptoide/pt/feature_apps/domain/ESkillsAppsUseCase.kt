package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ESkillsAppsUseCase @Inject constructor(
  private val appsRepository: AppsRepository,
  private val urlsCache: UrlsCache
) : AppsListUseCase {

  /**
   * [source] - unused
   */
  override suspend fun getAppsList(source: String): List<App> =
    appsRepository.getAppsList(
      storeId = 15,
      groupId = 14169744,
      bypassCache = urlsCache.isInvalid(id = "eSkills/15/14169744")
    )
}
